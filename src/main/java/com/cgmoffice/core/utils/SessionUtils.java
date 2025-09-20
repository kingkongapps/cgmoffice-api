package com.cgmoffice.core.utils;

import java.util.Arrays;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SessionUtils {

	/**
	 * attribute 값을 가져 오기 위한 method
	 */
	public Object getAttribute(String name) {
		return RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
	}

	/**
	 * attribute 설정 method
	 */
	public void setAttribute(String name, Object object) {
		try {
			RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
		} catch (IllegalStateException ise) {
			log.debug("### Error Message: {}");
		}
	}

	/**
	 * 설정한 attribute 삭제
	 */
	public void removeAttribute(String name) {
		RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
	}

	/**
	 * 세션의 모든값 지우기
	 */
	public void clearAllAttribute() {
		Arrays.asList(RequestContextHolder.getRequestAttributes().getAttributeNames(RequestAttributes.SCOPE_SESSION))
			.forEach(a -> removeAttribute(a));
	}

}
