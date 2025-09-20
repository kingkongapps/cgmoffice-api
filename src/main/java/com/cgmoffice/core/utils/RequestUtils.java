package com.cgmoffice.core.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cgmoffice.api.common.dto.UserDto;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@UtilityClass
public class RequestUtils {

	/**
	 * attribute 값을 가져 오기 위한 method
	 */
	public Object getAttribute(String name) {
		return RequestContextHolder.currentRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * attribute 설정 method
	 */
	public void setAttribute(String name, Object object) {
		try {
			RequestContextHolder.currentRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_REQUEST);
		} catch (IllegalStateException ise) {
			log.debug("### Error Message: {}");
		}
	}

	/**
	 * 설정한 attribute 삭제
	 */
	public void removeAttribute(String name) {
		RequestContextHolder.currentRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

	public UserDto getUser() {
		UserDto rslt = (UserDto) getAttribute("userDto");
	    if (rslt == null) {
	        rslt = UserDto.builder()
	                .memId("NO_USER")
	                .build();
	    }
	    return rslt;
	}
}
