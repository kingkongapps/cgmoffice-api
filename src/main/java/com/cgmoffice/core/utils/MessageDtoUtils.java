package com.cgmoffice.core.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cgmoffice.core.dto.ResCommonBaseDto;

import lombok.experimental.UtilityClass;


@UtilityClass
public class MessageDtoUtils {

	@SuppressWarnings("unchecked")
	public <T> ResCommonBaseDto<T> makeSuccessReturnDto(final T rvData) {
		return (ResCommonBaseDto<T>) ResCommonBaseDto.builder()
				.httpStatus(String.valueOf(HttpStatus.OK.value()))
				.responseCode(MessageI18nUtils.getMessage("global.success.code"))
				.responseMessage(MessageI18nUtils.getMessage("global.success.message"))
				.responseData(rvData)
				.build();
	}

	@SuppressWarnings("unchecked")
	public <T> ResCommonBaseDto<T> makeSuccessReturnDto(final String httpStatus, final T rvData) {
		return (ResCommonBaseDto<T>) ResCommonBaseDto.builder()
				.httpStatus(httpStatus)
				.responseCode(MessageI18nUtils.getMessage("global.success.code"))
				.responseMessage(MessageI18nUtils.getMessage("global.success.message"))
				.responseData(rvData)
				.build();
	}

	@SuppressWarnings("unchecked")
	public <T> ResCommonBaseDto<T> makeSuccessReturnDto(final String responseCode, final String responseMessage, final T rvData) {
		return (ResCommonBaseDto<T>) ResCommonBaseDto.builder()
				.httpStatus(String.valueOf(HttpStatus.OK.value()))
				.responseCode(responseCode)
				.responseMessage(responseMessage)
				.responseData(rvData)
				.build();
	}

	@SuppressWarnings("unchecked")
	public <T> ResCommonBaseDto<T> makeSuccessReturnDto(final String httpStatus, final String responseCode, final String responseMessage, final T rvData) {
		return (ResCommonBaseDto<T>) ResCommonBaseDto.builder()
				.httpStatus(httpStatus)
				.responseCode(responseCode)
				.responseMessage(responseMessage)
				.responseData(rvData)
				.build();
	}


	public ResCommonBaseDto<Object> makeError400ReturnDto(final String resCode, final String resMsg) {
		return ResCommonBaseDto.builder()
				.httpStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()))
				.responseCode(resCode)
				.responseMessage(resMsg)
				.build();
	}

	public ResCommonBaseDto<Object> makeError500ReturnDto(final String resCode, final String resMsg) {
		return ResCommonBaseDto.builder()
				.httpStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
				.responseCode(resCode)
				.responseMessage(resMsg)
				.build();
	}

	public ResCommonBaseDto<Object> makeErrorXxxReturnDto(final int httpStatus, final String resCode, final String resMsg) {
		return ResCommonBaseDto.builder()
				.httpStatus(String.valueOf(httpStatus))
				.responseCode(resCode)
				.responseMessage(resMsg)
				.build();
	}

	public String getErrorMsgFromMessageSource(final String errorValidKey, final List<String> errParams) {
		String[] errArgs = errParams.toArray(new String[0]);
		if(ArrayUtils.isEmpty(errArgs)) {
			return MessageI18nUtils.getMessage(errorValidKey);
		}
		return MessageI18nUtils.getMessage(errorValidKey, errArgs);
	}


	/**
	 * DTO -> MultiValueMap 변환 (Resttemplate GET방식에서 사용)<br>
	 * <주의> 단일 필드로 이루어진 "Query string"만 변환 가능!
	 *
	 * @param objectMapper
	 * @param reqDto
	 * @return
	 */
    public MultiValueMap<String, String> makeMultiValueMap(ObjectMapper objectMapper, Object reqDto) {
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    	try {
            Map<String, String> map = objectMapper.convertValue(reqDto, new TypeReference<Map<String, String>>() {});
            params.setAll(map);
        } catch (Exception e) {
            throw new IllegalStateException("Url Parameter 변환 오류!");
        }
    	return params;
    }

}
