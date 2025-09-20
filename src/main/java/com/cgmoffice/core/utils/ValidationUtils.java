package com.cgmoffice.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;

import com.cgmoffice.core.constant.BaseResponseCode;
import com.cgmoffice.core.dto.ResCommonBaseDto;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller로 입력되는 DTO에 대한 validation을 지원하기 Utility class.
 * 처리 가능 method(annotation) - POST(@RequestBody), GET(@ModelAttribute)
 */
@Slf4j
@UtilityClass
public class ValidationUtils {

	public final String BD_ERROR_KEY_PATTERN     = "^\\{([a-zA-Z\\.-]+)\\}$";
	public final String BD_ERROR_KEY_2_PATTERN   = "(.*)(\\[[0-9a-zA-Z\\,.-]\\])*\\{([a-zA-Z\\.-]+)\\}";
	public final String BD_ERROR_PARAM_1_PATTERN = "\\[([0-9a-zA-Z\\,.-]+)\\]"; // same as "\\[([\\w\\,.-]+)\\]"
	public final String BD_ERROR_PARAM_2_PATTERN = "\\{([0-9a-zA-Z\\.-]+)\\}$";

	public final Pattern NORMAL_ERR_MSG_PATTERN = Pattern.compile("\\{([0-9]+)?\\}");
	public final Pattern PARAM_ERR_MSG1_PATTERN = Pattern.compile("\\[([0-9, ]+)\\]");
	public final Pattern PARAM_ERR_MSG2_PATTERN = Pattern.compile("\\[([0-9, ]+)\\]\\s*([ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z]+.*)$");


	public List<String> getFieldErrorArgs(final FieldError flError) {
		List<String> fieldErrObjs = new ArrayList<>();
		for(Object feObj : flError.getArguments()) {
			String fieldVal = String.valueOf(feObj);
			//min, max 인경우 max가 앞에 온다.
//			log.debug(">>> Field Error Object: {}", feObj);
			if( StringUtils.startsWith(fieldVal, "org.springframework.") || fieldVal.length() > 10 ) {
				continue;
			}
			fieldErrObjs.add(fieldVal);
		}
		log.debug(">>> Field Error fieldErrObjs: {}", fieldErrObjs);
		return fieldErrObjs;
	}

	/**
	 * error Message 리턴. [POST방식]
	 *
	 * @methodName : getValidationErrorResponse
	 * @author     : E123531
	 * @date       : 2022.09.01
	 */
	public String getValidationErrorMessage(final FieldError flError) {
		List<String> fieldErrObjs = ValidationUtils.getFieldErrorArgs(flError);
		String rvErrorMsg;
		String errorValidKey = flError.getDefaultMessage();
		if(ValidationUtils.isValidationError(errorValidKey)) {
			rvErrorMsg = getAndSetErrorMessageFromMessagSource(flError, fieldErrObjs, errorValidKey);
		} else {
			rvErrorMsg = BaseResponseCode.FK10001.msg();
		}
		return rvErrorMsg;
	}

	/**
	 * ResponseEntity 전체 리턴. [POST방식]
	 *
	 * @methodName : getValidationErrorResponse
	 * @author     : E123531
	 * @date       : 2022.09.01
	 */
	public Object getValidationErrorResponse(final String errorValidCode, final FieldError flError) {
		String errPrefix;
		String rvErrorMsg;
		List<String> fieldErrObjs = ValidationUtils.getFieldErrorArgs(flError);
		String errorValidKey      = flError.getDefaultMessage();

		log.debug(">>> fieldErrObjs::{}, errorValidKey::{}", fieldErrObjs, errorValidKey);
		if(StringUtils.isNotBlank(errorValidKey) && !CollectionUtils.isEmpty(fieldErrObjs)) {
			// Error메시지의 arguments 있는 경우!
			errPrefix = ValidationUtils.isValidationErrorWithPrefix(errorValidKey).stream()
                                       .filter(Objects::nonNull).findFirst().orElse("");
			errorValidKey = isMatched4GetMethodParam(ValidationUtils.BD_ERROR_PARAM_2_PATTERN, errorValidKey);
			rvErrorMsg = errPrefix + getAndSetErrorMessageFromMessagSource(flError, fieldErrObjs, errorValidKey);
			// POST방식에서 Query String의 parameter validation 추가. ////////////////////////////////////////////////
			String qStrErrorMsg = checkQueryStrOfPostMethod(flError.getDefaultMessage(), fieldErrObjs, errorValidKey);
			rvErrorMsg = StringUtils.isBlank(qStrErrorMsg) ? rvErrorMsg : qStrErrorMsg;

			log.debug(">>>[1] rvErrorMsg::{} / errPrefix::{}", rvErrorMsg, errPrefix);
			return new ResponseEntity<>(MessageDtoUtils.makeError400ReturnDto(errorValidCode, rvErrorMsg), HttpStatus.BAD_REQUEST);
		} else if(StringUtils.isNotBlank(errorValidKey)) {
			// Error메시지의 arguments 없는 경우! - POST method의 경우에도 message args 넣어준다.
			String ptParams = isMatched4GetMethodParam(ValidationUtils.BD_ERROR_PARAM_1_PATTERN, errorValidKey);
			List<String> errParams = StringUtils.isBlank(ptParams) ? new ArrayList<>() : Arrays.asList(ptParams.split(","));
			List<String> errMsgList = ValidationUtils.isValidationErrorWithPrefix(errorValidKey);
			errPrefix = errMsgList.get(0); //errMsgList.get(1) is errorValidKey
			if(StringUtils.isNotBlank(errPrefix)) {
				rvErrorMsg = errPrefix + MessageDtoUtils.getErrorMsgFromMessageSource(errMsgList.get(1), errParams);
			} else { // LocalValidatorFactoryBean을 리턴하는 getValidator() Bean을 등록한 경우!
				rvErrorMsg = MessageI18nUtils.getExtMessage(errorValidKey);
			}
			log.debug(">>>[2] rvErrorMsg::{} / errPrefix::{}", rvErrorMsg, errPrefix);
			return new ResponseEntity<>(MessageDtoUtils.makeError400ReturnDto(errorValidCode, rvErrorMsg), HttpStatus.BAD_REQUEST);
		} else {
			//------------------------------------------------------------------------------------ ------------------
//			rvErrorMsg = BaseResponseCode.F11001.msg();
//			return new ResponseEntity<>(MessageDtoUtils.makeError500ReturnDto(BaseResponseCode.F11001.code(), rvErrorMsg)
//						, HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(MessageDtoUtils.makeError400ReturnDto(BaseResponseCode.FK40000.code(), errorValidKey)
					, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * POST방식에서 Query String의 parameter validation하는 경우.
	 * controller에서 '@Valid @ModelAttribute Object reqDto'의 형식.
	 */
	private String checkQueryStrOfPostMethod(final String errorMsg, List<String> fieldErrObjs, String errorValidKey) {
		String rvErrorMsg = null;
//		log.debug(">>>> errorMsg:::{}\n errorValidKey:{}\n fieldErrObjs::{}", errorMsg, errorValidKey, fieldErrObjs);
		// 1) 정규식이면서 정상 Error Message //////////////////////////////////////////////////////////////
		if(CoreStringUtils.isRegExp(fieldErrObjs.get(0)) && StringUtils.isNotBlank(errorMsg) && StringUtils.isBlank(errorValidKey)) {
			rvErrorMsg = errorMsg;
		}
		// 2) 정상 Error Message에서 binding 되지 않은 parameter가 있는 경우 //////////////////////////////
		//  여기의 errorMsg는 'validation.two.length' 형식이 아니고 parameter가 binding만 안된 문자열이기 때문에
		//  'MessageDtoUtils.getErrorMsgFromMessageSource(errorMsg, fieldErrObjs)'를 사용할 수 없음!
		Matcher mch	= NORMAL_ERR_MSG_PATTERN.matcher(errorMsg);
		int fieldErrObjsSize = fieldErrObjs.size(); // 해당list가 validation 어노테이션의 'min, max'등의 값들.
		if( (fieldErrObjsSize > 0) && mch.find() ) {
			StringBuffer sb = new StringBuffer();
			do {
				String	refValue = fieldErrObjs.get(--fieldErrObjsSize);
				mch.appendReplacement(sb, Matcher.quoteReplacement(refValue));
			} while(mch.find());
			mch.appendTail(sb);
			rvErrorMsg = sb.toString();
//			log.debug(">>> rvErrorMsg of mch::: {}", rvErrorMsg);
		}
		return rvErrorMsg;
	}

	private String getAndSetErrorMessageFromMessagSource(final FieldError flError, List<String> fieldErrObjs, String errorValidKey) {
		String rvErrorMsg;
		try {
			rvErrorMsg = setErrorMessageFromMessageSource(flError, fieldErrObjs, errorValidKey);
		} catch(NullPointerException ne) {
			rvErrorMsg = BaseResponseCode.FK40000.msg();
		}
		return rvErrorMsg;
	}

	/**
	 * ResponseEntity 전체 리턴. [GET방식]
	 *
	 * @methodName : getValidationErrorResponse
	 * @author     : E123531
	 * @date       : 2022.09.01
	 */
	public ResponseEntity<ResCommonBaseDto<Object>> getValidationErrorResponse4GetMethod(final String errorValidCode, final String fullErrorMsg) {
		String errPrefix;
		String rvErrorMsg;
		String ptParams = isMatched4GetMethodParam(ValidationUtils.BD_ERROR_PARAM_1_PATTERN, fullErrorMsg);
		String errorValidKey = isMatched4GetMethodParam(ValidationUtils.BD_ERROR_PARAM_2_PATTERN, fullErrorMsg);
		List<String> errParams = StringUtils.isBlank(ptParams) ? new ArrayList<>() : Arrays.asList(ptParams.split(","));

		Matcher fullStrMatcher = PARAM_ERR_MSG2_PATTERN.matcher(fullErrorMsg);
		boolean isErrorMsgWithFullStr = fullStrMatcher.find(); //fullErrorMsg가 message.properties에 있는 code값이 아닌 경우.

		log.debug(">>> fullErrorMsg:::{}", fullErrorMsg);
		if(StringUtils.isNotBlank(errorValidKey) && !CollectionUtils.isEmpty(errParams)) {
			// Error메시지의 arguments 있는 경우!
			errPrefix = ValidationUtils.isValidationErrorWithPrefix(fullErrorMsg).stream()
					                   .filter(Objects::nonNull).findFirst().orElse("");
			rvErrorMsg = errPrefix + MessageDtoUtils.getErrorMsgFromMessageSource(errorValidKey, errParams);
			log.debug(">>>[3] rvErrorMsg::{} / errPrefix::{}", rvErrorMsg, errPrefix);
		} else if(StringUtils.isNotBlank(errorValidKey) && Boolean.FALSE.equals(isErrorMsgWithFullStr)) {
			// Error메시지의 arguments 없는 경우!
			List<String> errMsgList = ValidationUtils.isValidationErrorWithPrefix(fullErrorMsg);
			errPrefix = errMsgList.get(0); //errMsgList.get(1) is errorValidKey
			rvErrorMsg = errPrefix + MessageDtoUtils.getErrorMsgFromMessageSource(errMsgList.get(1), errParams);

			String qStrErrorMsg = getErrorMsgWithQueryParams(fullErrorMsg, rvErrorMsg, errorValidKey);
			rvErrorMsg = StringUtils.isBlank(qStrErrorMsg) ? rvErrorMsg : qStrErrorMsg;

			log.debug(">>>[4] rvErrorMsg::{} / errPrefix::{}", rvErrorMsg, errPrefix);
		} else if(isErrorMsgWithFullStr) {
			// GET방식에서 rvErrorMsg가 message.properties에 있는 code값이 아닌경우.
			errorValidKey = "isValid_By_Matcher";
			rvErrorMsg = fullStrMatcher.group(2); // "입력 문자 길이는 {0}이상 {1}이하"
			String qStrErrorMsg = getErrorMsgWithQueryParams(fullErrorMsg, rvErrorMsg, errorValidKey);
			rvErrorMsg = StringUtils.isBlank(qStrErrorMsg) ? rvErrorMsg : qStrErrorMsg;

			log.debug(">>>[5] rvErrorMsg::{}", rvErrorMsg);
		} else if(StringUtils.isNotBlank(errorValidCode) && StringUtils.isNotBlank(fullErrorMsg)) {
			// fullErrorMsg를 parsing할 필요가 없는 경우!
			log.debug(">>>[6] rvErrorMsg::{}", fullErrorMsg);
			rvErrorMsg = fullErrorMsg;
		} else {
			log.debug(">>>[7] rvErrorMsg::{}", BaseResponseCode.FE40000.msg());
			rvErrorMsg = BaseResponseCode.FE40000.msg();
		}
		String rvErrorCode = StringUtils.isNotBlank(errorValidCode) ?  errorValidCode : BaseResponseCode.FE40000.code();

		return new ResponseEntity<>(MessageDtoUtils.makeError400ReturnDto(rvErrorCode, rvErrorMsg), HttpStatus.BAD_REQUEST);
	}


	private String getErrorMsgWithQueryParams(final String fullErrorMsg, String rvErrorMsg, String errorValidKey) {
		String qStrErrorMsg = null;
		Matcher mch = PARAM_ERR_MSG1_PATTERN.matcher(fullErrorMsg);
		if( mch.find() ) {
			String group = mch.group(1); // [min, max]
			List<String> fieldErrArgs = Arrays.stream(group.split(",")).map(String::trim).collect(Collectors.toList());
			Collections.reverse(fieldErrArgs); // list 순서 변경.
			qStrErrorMsg = checkQueryStrOfPostMethod(rvErrorMsg, fieldErrArgs, errorValidKey);
		}
		return qStrErrorMsg;
	}


	public boolean isValidationError(final String errorValidKey) {
		log.debug(">>> errorValidKey::{}", errorValidKey);
		Pattern pattern = Pattern.compile(BD_ERROR_KEY_PATTERN);
		Matcher matcher = pattern.matcher(errorValidKey);
		return matcher.find();
	}

	public List<String> isValidationErrorWithPrefix(final String errorValidKey) {
		log.debug(">>> errorValidKeyWithPrefix::{}", errorValidKey);
		// default Error Message 세팅!
		String prefix    = "";
		String errMsgKey = "global.error-validation.message";
		List<String> rv = new ArrayList<>();

		Pattern pattern = Pattern.compile(BD_ERROR_KEY_2_PATTERN);
		Matcher matcher = pattern.matcher(errorValidKey);
		int mGpCnt = matcher.groupCount();
		if(matcher.find()) {
			prefix = StringUtils.substringBefore(matcher.group(mGpCnt - 2), "[");
			errMsgKey = matcher.group(mGpCnt);
		}
		log.debug(">>> prefix:{}, errMsgKey:{}", prefix, errMsgKey);
		rv.add(prefix); rv.add(errMsgKey);

		return rv;
	}


	private String setErrorMessageFromMessageSource(final FieldError flError,
										final List<String> fieldErrObjs, final String errorValidKey) {
		String rvErrorMsg;
		String errorField = flError.getField();
		String pErrorValidKey = errorValidKey.replaceAll(ValidationUtils.BD_ERROR_KEY_PATTERN, "$1");
		log.debug(">>> errorField:{}, errorValidKey:{}", errorField, pErrorValidKey);
		Collections.reverse(fieldErrObjs); // min, max 순서로 변경!
		rvErrorMsg = MessageDtoUtils.getErrorMsgFromMessageSource(pErrorValidKey, fieldErrObjs);
		/* XXX: error message에 컬럼명 추가할 경우! */
//		rvErrorMsg = new StringBuffer("[").append(flError.getField()).append("] ").append(rvErrorMsg).toString();
		log.debug(">>> errorValidMsg::{}", rvErrorMsg);
		return rvErrorMsg;
	}

	protected String isMatched4GetMethodParam(final String pt, final String targetStr) {
		String rv = null;
		if(StringUtils.isBlank(targetStr)) {
			return rv;
		}
		Pattern pattern = Pattern.compile(pt);
		Matcher m = pattern.matcher(targetStr);
		String extStr = null;
		if(m.find()) {
			extStr = m.group();
			rv = RegExUtils.removeAll(extStr, "[\\{\\}\\[\\]]");
		}

		return rv;
	}

}
