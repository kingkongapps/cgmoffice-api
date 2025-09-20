package com.cgmoffice.core.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cgmoffice.core.service.LoggingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OncePerRequestFilter 는 HTTP 요청 당 한 번만 실행되도록 보장
 * 모든 요청에 대해서 요청당 한번만 로깅처리하기 위한 필터
 * 고로 리다이렉트나 포워드요청은 제외된다.
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingInterceptorFilter extends OncePerRequestFilter {
	private final LoggingService loggingService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		long reqTime = System.currentTimeMillis();

		// 로그용 thread 아이디 생성및 부여
		MDC.put(LoggingService.LOG_ID_NAME, genGlTxId());

		// 최초요청시 로그출력
		loggingService.displayReq(request);

		filterChain.doFilter(request, response);

		// 최종응답시 로그출력
		loggingService.displayResp(request, response, null, reqTime);

		MDC.clear();
	}

	private String genGlTxId() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
	}
}