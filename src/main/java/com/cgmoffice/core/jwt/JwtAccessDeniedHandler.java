package com.cgmoffice.core.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cgmoffice.core.constant.BaseResponseCode;
import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.service.LoggingService;
import com.cgmoffice.core.utils.MessageDtoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 필요한 권한이 없이 접근하려할때 어떻게 응답할지를 결정해서 응답을 반환하기 위한 기능을 구현함
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;
	private final LoggingService loggingService;


	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {

		// 페이지 호출의 경우는 로그인 페이지로 돌린다.
    	String accept = request.getHeader("Accept");
    	if (accept != null && accept.contains("text/html")) {
    		response.sendRedirect(CoreConstants.LOGIN_PAGE);
    		return;
    	}

		log.debug(">>> request.getLocalAddr::[{}]", request.getLocalAddr());
		log.debug(">>> RequestURI::[{}]", request.getRequestURI());
		log.debug(">>> RequestURL::[{}]", request.getRequestURL());

		// 로그를 출력한다.
		loggingService.displayResp(request, response, null);

		// 리턴할 메세지를 json 방식으로 작성한뒤 response에 실어서 리턴한다.
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.getWriter().println(objectMapper.writeValueAsString(
					MessageDtoUtils.makeErrorXxxReturnDto(HttpStatus.FORBIDDEN.value(), BaseResponseCode.FK40403.code(), BaseResponseCode.FK40403.msg()) ));

	}

}