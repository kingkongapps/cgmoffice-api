package com.cgmoffice.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.KotlinDetector;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ContinuationHandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewResolverMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;

import com.cgmoffice.core.argument_resolver.CoreArgumentResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoreWebMvcConfig extends WebMvcConfigurationSupport implements BeanFactoryAware {

	private ConfigurableBeanFactory beanFactory;
	private List<Object> requestResponseBodyAdvice = new ArrayList<>();

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	public ConfigurableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	@Override
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter(
			ContentNegotiationManager contentNegotiationManager,
			FormattingConversionService conversionService,
			Validator validator) {

		RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter(contentNegotiationManager, conversionService, validator);

		List<HandlerMethodArgumentResolver> resolvers = Optional
				.ofNullable(adapter.getArgumentResolvers())
				.orElse(new ArrayList<>());

		requestResponseBodyAdvice.addAll(Collections.singletonList(new JsonViewRequestBodyAdvice()));
		requestResponseBodyAdvice.addAll(Collections.singletonList(new JsonViewResponseBodyAdvice()));

		// framework core argument resolution
		resolvers.add(new CoreArgumentResolver());

		// org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.getDefaultArgumentResolvers
		// 내용을 다시 셋팅
		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new PathVariableMapMethodArgumentResolver());
		resolvers.add(new MatrixVariableMethodArgumentResolver());
		resolvers.add(new MatrixVariableMapMethodArgumentResolver());
		resolvers.add(new ServletModelAttributeMethodProcessor(false));
		resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), requestResponseBodyAdvice));
		resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters(), requestResponseBodyAdvice));
		resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new RequestHeaderMapMethodArgumentResolver());
		resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());


		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());
		resolvers.add(new HttpEntityMethodProcessor(getMessageConverters(), requestResponseBodyAdvice));
		resolvers.add(new RedirectAttributesMethodArgumentResolver());
		resolvers.add(new ModelMethodProcessor());
		resolvers.add(new MapMethodProcessor());
		resolvers.add(new ErrorsMethodArgumentResolver());
		resolvers.add(new SessionStatusMethodArgumentResolver());
		resolvers.add(new UriComponentsBuilderMethodArgumentResolver());
		if (KotlinDetector.isKotlinPresent()) {
			resolvers.add(new ContinuationHandlerMethodArgumentResolver());
		}
		// Catch-all
		resolvers.add(new PrincipalMethodArgumentResolver());
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
		resolvers.add(new ServletModelAttributeMethodProcessor(true));

		adapter.setArgumentResolvers(resolvers);


		List<HandlerMethodReturnValueHandler> handlers = Optional
				.ofNullable(adapter.getReturnValueHandlers())
				.orElse(new ArrayList<>());

		// framework core return value types

		// org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.getDefaultReturnValueHandlers
		// 내용을 재셋팅
		// Single-purpose return value types
		handlers.add(new ModelAndViewMethodReturnValueHandler());
		handlers.add(new ModelMethodProcessor());
		handlers.add(new ViewMethodReturnValueHandler());
		handlers.add(new ResponseBodyEmitterReturnValueHandler(getMessageConverters(),
				adapter.getReactiveAdapterRegistry(), new SimpleAsyncTaskExecutor("MvcAsync"), contentNegotiationManager));
		handlers.add(new StreamingResponseBodyReturnValueHandler());
		handlers.add(new HttpEntityMethodProcessor(getMessageConverters(),
				contentNegotiationManager, requestResponseBodyAdvice));
		handlers.add(new HttpHeadersReturnValueHandler());
		handlers.add(new CallableMethodReturnValueHandler());
		handlers.add(new DeferredResultMethodReturnValueHandler());
		handlers.add(new AsyncTaskMethodReturnValueHandler(this.beanFactory));
		// Annotation-based return value types
		handlers.add(new ServletModelAttributeMethodProcessor(false));
		handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(),
				contentNegotiationManager, requestResponseBodyAdvice));
		// Multi-purpose return value types
		handlers.add(new ViewNameMethodReturnValueHandler());
		handlers.add(new MapMethodProcessor());
		// Catch-all
		if (!CollectionUtils.isEmpty(adapter.getModelAndViewResolvers())) {
			handlers.add(new ModelAndViewResolverMethodReturnValueHandler(adapter.getModelAndViewResolvers()));
		}
		else {
			handlers.add(new ServletModelAttributeMethodProcessor(true));
		}
		adapter.setReturnValueHandlers(handlers);

		return adapter;
	}

	protected void addArgumentResolver(RequestMappingHandlerAdapter adapter, HandlerMethodArgumentResolver resolver) {
		List<HandlerMethodArgumentResolver> oldResolvers = adapter.getArgumentResolvers();

		List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<>();
		newResolvers.add(resolver);
		oldResolvers.forEach(a -> newResolvers.add(a));
		adapter.setArgumentResolvers(newResolvers);
	}

	protected void addReturnValueHandler(RequestMappingHandlerAdapter adapter, HandlerMethodReturnValueHandler handler) {
		List<HandlerMethodReturnValueHandler> oldHandlers = adapter.getReturnValueHandlers();

		List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
		newHandlers.add(handler);
		oldHandlers.forEach(a -> newHandlers.add(a));
		adapter.setReturnValueHandlers(newHandlers);
	}
}
