package com.cgmoffice.core.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import com.cgmoffice.core.utils.ApplicationContextProvider;

@Configuration
public class GeneralConfig {


	/**
	 * CoreUtil 등에서 getBean 메소드가 가능하도록 하기 위해
	 * @return
	 */
	@Bean
	ApplicationContextProvider applicationContextProvider() {
		return new ApplicationContextProvider();
	}

	/**
	 * spring security 의 authenticationprovider 에서도 request 를 가져올수 있게 하기 위해
	 * @return
	 */
	@Bean
	ServletListenerRegistrationBean<RequestContextListener> requestContextListenerRegistration() {
	  return new ServletListenerRegistrationBean<>(new RequestContextListener());
	}
}
