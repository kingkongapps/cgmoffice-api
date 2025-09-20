package com.cgmoffice.core.interceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.cgmoffice.api.common.service.AuthService;
import com.cgmoffice.core.jwt.TokenProvider;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.SessionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultInterceptor implements AsyncHandlerInterceptor {

	private final CmmnProperties properties;
	private final TokenProvider tokenProvider;
	private final AuthService authService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

//		String requestURI = request.getRequestURI();
//		log.debug(">>> requestURI: {}", requestURI);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String jwt = CoreUtils.resolveToken(request, response);

		// 로그인이 되어있는 경우
		if(authentication != null
				&& StringUtils.isNotEmpty(jwt)
				&& tokenProvider.validateToken(jwt)) {
			// 호출때마다 jwt 를 갱신해서 프론트에 보낸다.
			jwt = tokenProvider.createToken(authentication);

			response.setHeader("jwt", jwt);

	        Cookie cookie = new Cookie("jwt", jwt);
	        cookie.setHttpOnly(true); // JS에서 접근 못 하게
//	        cookie.setSecure(true); // HTTPS일 때만 전송
	        cookie.setPath("/"); // 전체 경로에 대해 유효
	        response.addCookie(cookie);
		}

		authService.getCmmnProperties(request, response);

		SessionUtils.setAttribute("properties", properties);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}
