package com.cgmoffice.core.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.cgmoffice.core.utils.CoreStringUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.cgmoffice.core.utils.CoreStringUtils;

@Component
@Order(2)
public class XSSRequestProtectFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest)request);

		byte[] bary = requestWrapper.getByteArray();
		if(bary==null) {
			chain.doFilter(request, response);
		} else {
			chain.doFilter(requestWrapper, response);
		}
	}

	public final static class RequestWrapper extends HttpServletRequestWrapper {
		private byte[] bary;
		public RequestWrapper(HttpServletRequest servletRequest) throws IOException {
			super(servletRequest);
			if (servletRequest.getContentType() != null && servletRequest.getContentType().indexOf("json") > -1) {
				bary = cleanXSS(getBody(servletRequest)).getBytes(StandardCharsets.UTF_8);
			}
		}

		public byte[] getByteArray() {

			byte[] rslt = null;
			if(bary != null) {
				rslt = new byte[bary.length];
				for(int i = 0; i<bary.length; i++) {
					rslt[i] = bary[i];
				}
			}

			return rslt;
		}

		@Override
		public String[] getParameterValues(String parameter) {

			String[] values = super.getParameterValues(parameter);
			if (values == null) {
				return null;
			}
			int count = values.length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = cleanXSS(values[i]);
			}
			return encodedValues;
		}

		@Override
		public String getParameter(String parameter) {
			String value = super.getParameter(parameter);
			if (value == null) {
				return null;
			}
			return cleanXSS(value);
		}

		public String getHeader(String name) {
			String value = super.getHeader(name);
			if (value == null){
				return null;
			}
			return cleanXSS(value);

		}

		private static String cleanXSS(String value) {
			return CoreStringUtils.encodeXSS(value);
		}

		public static String getBody(HttpServletRequest request) throws IOException {

			try(BufferedReader input = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))){
				StringBuilder builder = new StringBuilder();
				String buffer;
				while (true) {
					buffer = input.readLine();
					if(buffer == null) {
						break;
					}
					if (builder.length() > 0) {
						builder.append("\n");
					}
					builder.append(buffer);
				}

				//input.close();
				return builder.toString();
			}
		}
	}
}
