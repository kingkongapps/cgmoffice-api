package com.cgmoffice.core.exception;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.api.common.service.AuthService;
import com.cgmoffice.api.common.service.UserService;
import com.cgmoffice.core.constant.BaseResponseCode;
import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.dto.ResCommonBaseDto;
import com.cgmoffice.core.dto.SysErrlogLstDto;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.JsonUtils;
import com.cgmoffice.core.utils.MessageDtoUtils;
import com.cgmoffice.core.utils.MessageI18nUtils;
import com.cgmoffice.core.utils.RequestUtils;
import com.cgmoffice.core.utils.ValidationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Controller
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandlerCtl {

	private final AppDao appDao;
	private final HttpServletRequest request;
	private final AuthService authService;
	private final UserService userService;

	/**
	 * Default exception
	 * @param ex
	 * @param response
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<Object> handleUserException(Exception ex){

		Throwable exRootCause = ExceptionUtils.getRootCause(ex);
//		if (!( exRootCause instanceof SQLException )) {
		String errDtl = CoreUtils.getExceptionStackTrace((Exception) exRootCause);
			log.error(errDtl);
//		}

		insertErrLog(ex);


		// AuthService 에서 오류발생시
		if ( ex instanceof InternalAuthenticationServiceException ) {
			//로그인 페이지에서 로그인 시도 했을 시, 로그인 로그를 쌓는다.
			if(RequestUtils.getUser().getViewId().equals(CoreConstants.LOGIN_PAGE)) {
				UserDto loginDto = new UserDto();
				loginDto.setMemId(RequestUtils.getUser().getMemId());
				loginDto.setLgnScsYn("N");

				// 로그인로그 삽입
				authService.insertLoginLog(loginDto);

				// 사용자 로그인 횟수와 최근 로그인 시간 업데이트
				userService.updateUserInfo(loginDto);
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(MessageDtoUtils.makeErrorXxxReturnDto(
							HttpStatus.UNAUTHORIZED.value(),
							MessageI18nUtils.getMessage("global.bad-credential-1.code"),
							ex.getMessage()));
		}
		// 파일이 존재하지 않을시 발생한 오류
		else if ( ex instanceof FileNotFoundException ) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageDtoUtils.makeErrorXxxReturnDto(
							HttpStatus.INTERNAL_SERVER_ERROR.value(),
							MessageI18nUtils.getMessage("global.error-file-not-found.code"),
							MessageI18nUtils.getMessage("global.error-file-not-found.message")));
		}
		// 로그인 비밀번호 오류시
		else if ( ex instanceof BadCredentialsException ) {

			UserDto loginDto = new UserDto();
			loginDto.setMemId(RequestUtils.getUser().getMemId());
			loginDto.setLgnScsYn("N");

			// 로그인로그 삽입
			authService.insertLoginLog(loginDto);

			// 사용자 로그인 횟수와 최근 로그인 시간 업데이트
			userService.updateUserInfo(loginDto);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageDtoUtils.makeErrorXxxReturnDto(
							HttpStatus.INTERNAL_SERVER_ERROR.value(),
							MessageI18nUtils.getMessage("global.bad-credential.code"),
							MessageI18nUtils.getMessage("global.bad-credential.message")));
		} else if ( exRootCause instanceof MethodArgumentNotValidException ) {

			Map<String, String> errors = new HashMap<>();
			MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) exRootCause;

			// 모든 FieldError를 추출
			methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
				String fieldName = ((FieldError) error).getField(); // 에러 필드 이름
				String errorMessage = error.getDefaultMessage(); // 기본 에러 메시지
				errors.put(fieldName, errorMessage);
			});

			log.error(">>> Controller 입력의 파라미터의 Validation 오류: {}", JsonUtils.toJsonStr(errors));

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageDtoUtils.makeError500ReturnDto(
							MessageI18nUtils.getMessage("global.error-validation.code"), MessageI18nUtils.getMessage("global.error-validation.message")));
		} else if ( exRootCause instanceof SQLException ) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageDtoUtils.makeError500ReturnDto(
							MessageI18nUtils.getMessage("global.error-sql-exec.code"), MessageI18nUtils.getMessage("global.error-sql-exec.message")));
		} else if( ex instanceof CmmnBizException ) {
			CmmnBizException cmmnBizException = (CmmnBizException) ex;
			return ResponseEntity.status(cmmnBizException.getHttpStatusCode()) // Utility class의 static 메소드에서 Exception을 던지는 경우!
					.body(MessageDtoUtils.makeErrorXxxReturnDto(
							cmmnBizException.httpStatusCode,
							cmmnBizException.getResponseCode(),
							cmmnBizException.getResponseMessage()));
		} else if ( exRootCause instanceof CmmnBizException ) {

			CmmnBizException cmmnBizException = (CmmnBizException) exRootCause;

			return ResponseEntity.status(cmmnBizException.httpStatusCode) // Utility class의 static 메소드에서 Exception을 던지는 경우!
					.body(MessageDtoUtils.makeErrorXxxReturnDto(
							cmmnBizException.httpStatusCode,
							cmmnBizException.getResponseCode(),
							cmmnBizException.getResponseMessage()));
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(MessageDtoUtils.makeError500ReturnDto(
							MessageI18nUtils.getMessage("global.error-server.code"),
							MessageI18nUtils.getMessage("global.error-server.message")
							)
						);
	}

	/**
	 * 404 Not Found Error에 대한 응답
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ResCommonBaseDto<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {

		insertErrLog(ex);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageDtoUtils.makeErrorXxxReturnDto(
					HttpStatus.NOT_FOUND.value(),BaseResponseCode.FK40404.code(),
					MessageI18nUtils.getMessage("global.error-not-found-url.message") + ":" + ex.getRequestURL()));
	}

	/**
	 * >>@Validated 에서 발생하는 Exception
	 *
	 * @methodName : handleConstraintViolationException
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResCommonBaseDto<Object>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

		insertErrLog(ex);

		String rvErrorMsg = ex.getConstraintViolations().stream()
							  .map(ConstraintViolation::getMessageTemplate).collect(Collectors.joining());
		log.debug(">>> errorValidKey:::{}", rvErrorMsg);
		// GET방식에 대한 메시지 처리!
		return  ValidationUtils.getValidationErrorResponse4GetMethod(BaseResponseCode.FK40000.code(), rvErrorMsg);
	}

	private void insertErrLog(Exception ex) {
		Throwable exRootCause = ExceptionUtils.getRootCause(ex);
		String errDtl = CoreUtils.getExceptionStackTrace((Exception) exRootCause);

		BigInteger fromTimestamp = new BigInteger(CoreConstants.callTimestampMillisecond.get());
		BigInteger toTimestamp = new BigInteger(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

		String memId = RequestUtils.getUser().getMemId();

		if(errDtl.length() > 3800) {
			errDtl = errDtl.substring(0, 3800);
		}

		SysErrlogLstDto sysErrlogLstDto = SysErrlogLstDto.builder()
				.errLogid(CoreUtils.genTimestampUniqId())
				.occurDtm(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
				.sysDlngCfcd("5")  // 1: 조회, 2:등록, 3:삭제, 4:수정, 5:처리
				.dlngTime(toTimestamp.subtract(fromTimestamp).toString())
				.reqr(memId)
				.errCd("500")
				.errDtl(errDtl)
				.menuNm("")
				.methNm(request.getRequestURI())
				.crtr(memId)
				.amdr(memId)
				.build();
		appDao.insert("globalexceptionhandler.insertErrLog", sysErrlogLstDto);
	}

}
