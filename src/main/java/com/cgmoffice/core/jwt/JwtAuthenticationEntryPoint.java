package com.cgmoffice.core.jwt;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cgmoffice.core.constant.BaseResponseCode;
import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.service.LoggingService;
import com.cgmoffice.core.utils.MessageDtoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증되지 않은 사용자가 인증이 필요한 리소스에 접근했을 때 어떻게 응답할지를 결정해서 응답을 반환하기 위한 기능을 구현함
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	private final LoggingService loggingService;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

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
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.getWriter().println(objectMapper.writeValueAsString(
					MessageDtoUtils.makeErrorXxxReturnDto(HttpStatus.UNAUTHORIZED.value(), BaseResponseCode.FK40401.code(), BaseResponseCode.FK40401.msg()) ));

	}

}
