package com.cgmoffice.core.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.jwt.TokenProvider;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * jwt 토큰관련 필터로, 이 필터는 SpringSecurityConfig.class 내의 설정에 의해 적용된다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;

	/**
	 * TOKEN의 인증정보를 SecurityContext 안에 저장하는 역할.
	 * 현재는 jwtFilter 통과 시 loadUserByUsername을 호출하여 DB를 거치지 않으므로 Security Context의 Entity 정보가 충분하지 않다.
	 * 즉 loadUserByUsername을 호출하는 인증 API를 제외하고는 sessionId, customerId만 가지고 있으므로 Account 정보가 필요하다면 DB or Redis 조회 필요
	 */
	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		CoreConstants.callTimestampMillisecond.set(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

		// request header 에서 jwt 토큰을 추출한다.
		String jwt = CoreUtils.resolveToken(request, response);

		if(StringUtils.isNotEmpty(jwt) && tokenProvider.validateToken(jwt)) {

			// jwt 토큰에서 Authentication 을 추출한다.
			Authentication authentication = tokenProvider.getAuthentication(jwt);
			// SecurityContextHolder에 Authentication 를 저장한다.
			SecurityContextHolder.getContext().setAuthentication(authentication);

			List<String> authorities = authentication.getAuthorities()
					.stream()
					.map(a -> a.getAuthority())
					.collect(Collectors.toList());

			UserDto userDto = UserDto.builder()
					.memId(authentication.getName())
					.authCd(authorities.get(0))
					.build();

			// request scope 에 사용자로그인 아이디를 셋팅한다.
			RequestUtils.setAttribute("userDto", userDto);

            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
		} else {
			// SecurityContextHolder 에 인증정보를 저장하지 않고 진행하기 때문에
			// SpringSecurityConfig 에서 authorizeHttpRequests 에서 인증이 필요한 uri에 대해서는 authenticationEntryPoint 에서 예외처리된다.
			log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
			// SpringSecurityConfig.java 를 통해 등록한 CorsFilter 는 serity filter 보다 순위가 뒤에있어서,
			// serity filter 에서 발생하는 exception이 발생할 경우 CorsFilter에 도달하지않고 바로 빠져나가기 때문에 이렇게 수동으로 등록을 한다.
			response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "*");
	        response.setHeader("Access-Control-Allow-Headers", "*");
	        response.setHeader("Access-Control-Allow-Credentials", "true");

	        SecurityContextHolder.clearContext();

	        // Preflight 요청 처리
	        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
	            response.setStatus(HttpServletResponse.SC_OK);
	            return;
	        }
        }

		filterChain.doFilter(request, response);
	}
}