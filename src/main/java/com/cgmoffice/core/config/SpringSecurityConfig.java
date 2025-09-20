package com.cgmoffice.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.filter.JwtFilter;
import com.cgmoffice.core.jwt.JwtAccessDeniedHandler;
import com.cgmoffice.core.jwt.JwtAuthenticationEntryPoint;
import com.cgmoffice.core.jwt.TokenProvider;
import com.cgmoffice.core.properties.CmmnProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final TokenProvider tokenProvider;
    private final CmmnProperties cmmnProperties;

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        // 스프링시큐리티의 간섭을 받지않는 경로들, 즉 JwtFilter 를 타지않는 경로들
        return (web) -> web.ignoring()
        		.requestMatchers(
        				new AntPathRequestMatcher("/static/**"),
        				new AntPathRequestMatcher("/favicon.ico")
        		);
    }

    // PasswordEncoder는 BCryptPasswordEncoder를 사용
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws
            Exception {

        http
        	// Authorization 헤더로 JWT token을 사용하는 방식이기 때문에 csrf를 disable 한다.
        	.csrf(csrfConfig -> csrfConfig.disable())
        	// HttpServletRequest를 사용하는 요청들에 대한 접근제한 설정로직
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests

				.requestMatchers("/sample/**").permitAll() // 샘플페이지
				.requestMatchers("/api/sample/**").permitAll() // 샘플 api
				.requestMatchers("/api/cnt/prdtClusPdfMng/indvClusRcv").permitAll() // 개별약관생성
				.requestMatchers("/api/cnt/prdtClusMng/batch").permitAll() // 배치실행

				.requestMatchers("/api/common/authenticate/signup").permitAll() // 사용자등록 api
				.requestMatchers("/api/common/authenticate/signin").permitAll() // 로그인 api
				.requestMatchers("/api/common/authenticate/getCmmnProperties").permitAll() // JsolProperties 정보 가져오기 [필요한 정보만]
				.requestMatchers("/api/common/authenticate/verify").authenticated() // 토큰검증
                //.requestMatchers("/api/common/utils/**").permitAll() // 공통 유틸 api
                .requestMatchers(
                		CoreConstants.LOGIN_PAGE // 로그인 페이지
                ).permitAll()
				.anyRequest().authenticated() // 그 외 인증 없이 접근불가
			)
			// 예외처리를 위한 로직
			.exceptionHandling(exceptionConfig ->
				exceptionConfig
				// 인증되지 않은 사용자가 인증이 필요한 리소스에 접근했을 때 어떻게 응답할지를 결정해서 응답을 반환
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				// 필요한 권한이 없이 접근하려할때
				.accessDeniedHandler(jwtAccessDeniedHandler)
			)
			// 세션관리를 위한 로직
//			.sessionManagement(sessionManagement ->
//				sessionManagement
//				// 세션을 사용하지 않기때문에 stateless 로 설정
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//			)
			// 헤더셋팅을 위한 로직
			.headers(headerConfig ->
				headerConfig.frameOptions(frameOptionsConfig ->
					// X-Frame-Options 헤더를 구성하는 코드
					// X-Frame-Options: SAMEORIGIN을 헤더로 추가
					// 브라우저가 웹페이지를 다른 웹사이트의 <iframe> 또는 프레임 내에서 동일 도메인에서만 접근이 가능하도록 설정
					frameOptionsConfig.sameOrigin()
				)
			)
			// jwt 필터를 적용한다.
			// 즉 security 필터보다 먼저 적용시킬 필터를 등록한다.
			.addFilterBefore(
					new JwtFilter(tokenProvider),
					UsernamePasswordAuthenticationFilter.class
					)
			;
        ;

        return http.build();
    }

	@Bean
	CorsFilter corsFilter() {
		// Spring에서 제공하는 클래스로, CORS 관련 설정을 캡슐화합니다.
		CorsConfiguration config = new CorsConfiguration();

		// 브라우저가 요청 시 쿠키나 인증 정보를 포함하도록 허용합니다.
		// 이 설정은 서버가 Access-Control-Allow-Credentials: true 헤더를 응답에 추가하도록 합니다.
		config.setAllowCredentials(true);

		// Access-Control-Allow-Origin: https://example.com 같이, 헤더에 허용하는 origin 을 지정합니다.
		config.setAllowedOrigins(cmmnProperties.getHttpCall().getAllowedOrigins());

		// 클라이언트가 어떤 HTTP 요청 헤더를 사용할 수 있는지를 지정
		// "*"로 설정하면 모든 헤더를 허용합니다.
		// 예를들면, Access-Control-Allow-Headers: Content-Type, Authorization 같이 헤더를 셋팅합니다.
		config.addAllowedHeader("*"); // 모든 HEADER에 응답을 허용.

		// 허용되는 HTTP 메서드(예: GET, POST, PUT 등)를 지정.
		// "*"로 설정하면 모든 HTTP 메서드(POST,GET,PUT,DELETE,PATCH,OPTIONS)를 허용합니다.
		// 예를 들면, Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS 같이 헤더를 셋팅합니다.
		config.addAllowedMethod("*");

		// 브라우저는 보안 정책(CORS) 때문에 서버에서 명시적으로 허용할 헤더를 지정한다.
		config.addExposedHeader("jwt");

		// 특정 URL 패턴에 대해 위에서 정의한 CORS 정책을 적용합니다.
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// 애플리케이션의 모든 URL 경로(/**)에 대해 정의된 CORS 설정(config)을 적용합니다.
		source.registerCorsConfiguration("/**", config);

		// CorsFilter는 Spring Security 또는 Spring Web에서 CORS 요청을 처리하는 필터
		// 이 필터는 HTTP 요청이 들어올 때 정의된 CorsConfiguration에 따라 요청을 허용하거나 차단합니다.
		return new CorsFilter(source);
	}

}
