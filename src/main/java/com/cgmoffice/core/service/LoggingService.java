package com.cgmoffice.core.service;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoggingService {

	private final FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
	public static final String LOG_ID_NAME = "glTxId";

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	public void displayReq(HttpServletRequest request) {
		String[] ignorePathList = {
				"/static/",
				"/favicon.ico",
		};

		String requestUri = request.getRequestURI();

		for (String ignorePath : ignorePathList) {
            if (requestUri.startsWith(ignorePath)) {
                return;
            }
        }
		StringBuilder reqMessage = new StringBuilder();

		String glTxId = MDC.get(LOG_ID_NAME); // request, response 동일.
		reqMessage.append("REQUEST ");
		reqMessage.append("globalTxId[").append(glTxId).append("]");
		reqMessage.append(" method = [").append(request.getMethod()).append("]");
		String queryString = request.getQueryString();
		String uri = (queryString == null ? requestUri : requestUri + queryString);
		reqMessage.append(" path = [").append(uri).append("] ");

//		Map<String, String> parameters = getParameters(request);
//		if (!parameters.isEmpty()) {
//			reqMessage.append("\n Rquest parameters = [").append(parameters).append("] ");
//		}

//		String contentType = request.getContentType();
//		ServletServerHttpRequest sshr = new ServletServerHttpRequest(request);
//		MediaType type = StringUtils.isNotBlank(contentType) ? MediaType.valueOf(contentType) : null;
//		if( formHttpMessageConverter.canRead(MultiValueMap.class, type) ) {
//			try {
//				MultiValueMap<String, String> body = formHttpMessageConverter.read(null, sshr);
//
//				if (!Objects.isNull(body)) {
//					reqMessage.append("\n Rquest body = [").append(body).append("]");
//				}
//			} catch (HttpMessageNotReadableException | IOException e) {
//				log.error(Throwables.getStackTraceAsString(e));
//			}
//
//		}

		log.info("$$###>Log Request: {}", reqMessage);
	}

	private String getRequestHeaderInfos(HttpServletRequest request) {
		Enumeration<String> reqHeaderNames = request.getHeaderNames();
		StringBuilder sb = new StringBuilder();
		while(reqHeaderNames.hasMoreElements()) {
			String headName = reqHeaderNames.nextElement();
			String header = request.getHeader(headName);
			sb.append(headName).append(":").append(header).append(" ");
		}
		return sb.toString();
	}

	public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
		displayResp(request, response, body, -1);
	}


	public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body, long reqTime) {

		String[] ignorePathList = {
				"/static/",
				"/favicon.ico",
		};

		String requestUri = request.getRequestURI();

		for (String ignorePath : ignorePathList) {
            if (requestUri.startsWith(ignorePath)) {
                return;
            }
        }

		StringBuilder respMessage = new StringBuilder();
		String glTxId = MDC.get(LOG_ID_NAME); // request, response 동일.
		respMessage.append("RESPONSE ");
		respMessage.append("globalTxId[").append(glTxId).append("]");
//		respMessage.append(" method = [").append(request.getMethod()).append("]");
//		Map<String, String> headers = getHeaders(response);
//		if (!headers.isEmpty()) {
//			respMessage.append(" ResponseHeaders = [").append(CoreJsonUtils.toJsonStr(headers)).append("]");
//		}
//		if(body != null) {
//			respMessage.append(" responseBody = [").append(body).append("]");
//		}
		if(reqTime != -1) {
			respMessage.append(" RESPONSE_TIME: " + (System.currentTimeMillis() - reqTime) + "ms");
		}

		log.info("$$###>Log Response: {}", respMessage);
	}

	private Map<String, String> getHeaders(HttpServletResponse response) {
		Map<String, String> headers = new HashMap<>();
		Collection<String> headerMap = response.getHeaderNames();
		for (String str : headerMap) {
			headers.put(str, response.getHeader(str));
		}
		return headers;
	}

	private Map<String, String> getParameters(HttpServletRequest request) {
		Map<String, String> parameters = new HashMap<>();
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			String paramValue = request.getParameter(paramName);
			parameters.put(paramName, paramValue);
		}
		return parameters;
	}

}