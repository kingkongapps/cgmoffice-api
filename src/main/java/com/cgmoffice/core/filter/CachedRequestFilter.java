package com.cgmoffice.core.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.cgmoffice.core.properties.CmmnProperties;

import lombok.RequiredArgsConstructor;

/**
 * post로 전달된 request의 body 여러번 읽기가 가능하도록 하는 필터
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 이렇게 하면 Spring Security 필터보다도 앞서게 된다.
@RequiredArgsConstructor
public class CachedRequestFilter implements Filter {

	private final CmmnProperties cmmnProperties;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String header = ((HttpServletRequest) request).getHeader(HttpHeaders.CONTENT_TYPE);
		if (StringUtils.isNoneEmpty(header) && header.indexOf("multipart/form-data") != -1) {
			chain.doFilter(request, response);
		} else {
			CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(
					(HttpServletRequest) request);
			chain.doFilter(cachedBodyHttpServletRequest, response);
		}
	}

	public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

		private byte[] cachedBody;

		public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
			super(request);
			InputStream requestInputStream = request.getInputStream();
			this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {

//			String body = new String(cachedBody);
//
//			Map params = CoreUtils.cast(new TypeReference<Map<String, String>>() {}, body);
//			String encdata = (String) params.get("encdata");
//
//			if(StringUtils.isNotEmpty(encdata) ) {
//				String decbody = AES256Utils.aesDecode(encdata, cmmnProperties.getAes256Key());
//				return new CachedBodyServletInputStream(decbody.getBytes());
//			}

			return new CachedBodyServletInputStream(this.cachedBody);
		}

		@Override
		public BufferedReader getReader() throws IOException {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
			return new BufferedReader(new InputStreamReader(byteArrayInputStream));
		}

		public class CachedBodyServletInputStream extends ServletInputStream {

			private InputStream cachedBodyInputStream;

			public CachedBodyServletInputStream(byte[] cachedBody) {
				this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
			}

			@Override
			public boolean isFinished() {
				return cachedBody.length == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public int read() throws IOException {
				return cachedBodyInputStream.read();
			}
		}

	}
}
