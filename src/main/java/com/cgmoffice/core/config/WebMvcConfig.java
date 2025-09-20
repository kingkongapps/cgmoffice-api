package com.cgmoffice.core.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import com.cgmoffice.core.interceptor.DefaultInterceptor;
import com.cgmoffice.core.interceptor.LoggingInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig extends CoreWebMvcConfig {

	private final DefaultInterceptor defaultInterceptor;
	private final LoggingInterceptor loggingInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(defaultInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/static/**")
			;
		registry
			.addInterceptor(loggingInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/static/**")
			;

		// 특정 URI(/api/common/changLocale)의 lang 파라메타에 한해서만 locales 변경을 처리하도록 한다.
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // 요청 파라미터 이름 (기본값: "locale")
        registry
        	.addInterceptor(interceptor)
			.addPathPatterns("/api/common/changLocale")
        	;
	}

	@Override
	protected void addViewControllers(ViewControllerRegistry registry) {
		// favicon 요청 무시
		// registry.addViewController("/favicon.ico").setStatusCode(HttpStatus.NO_CONTENT);
	}

	@Override
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter(
			ContentNegotiationManager contentNegotiationManager, FormattingConversionService conversionService,
			Validator validator) {

		RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter(contentNegotiationManager, conversionService, validator);

		// 여기에서 추가되는 argument resolver를 추가한다.
//		addArgumentResolver(adapter, new DatasourceArgumentResolver());

		// 여기에서 추가되는 return value handler를 추가한다.
//		addReturnValueHandler(adapter, new DatasourceReturnValueHandler());

		return adapter;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/")
			;
		registry
			.addResourceHandler("/favicon.ico")
			.addResourceLocations("classpath:/static/ico/")
			;
	}

	@Bean
	BeanNameViewResolver beanNameViewResolver() {
		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(0);
		return beanNameViewResolver;
	}

	/**
	 * locale 변경이 가능한 localeResolver 로 변경한다.
	 */
	@Override
	@Bean
	public LocaleResolver localeResolver() {
	    CookieLocaleResolver resolver = new CookieLocaleResolver();
	    // 쿠키 이름 명시적으로 설정 (Spring 6에서 생성자에서 넣었던 것)
	    resolver.setCookieName("lang");
	    // 기본 locale 설정
	    resolver.setDefaultLocale(Locale.KOREA);
	    // 유지 시간: 초 단위 (2시간 = 7200초)
	    resolver.setCookieMaxAge(60 * 60 * 2);
	    return resolver;
	}
}
