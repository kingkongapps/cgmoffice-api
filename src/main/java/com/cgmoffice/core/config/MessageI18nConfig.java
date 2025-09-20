package com.cgmoffice.core.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.cgmoffice.core.utils.MessageI18nUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 다국어메세지 설정파일
 */
@Slf4j
@Configuration
public class MessageI18nConfig {

	// 기본 다국어메세지 파일목록
	private static final List<String> CORE_BASE_NAMES
		= Arrays.asList(
				"classpath:/i18n/glException",
				"classpath:/i18n/exception",
				"classpath:/i18n/messages"
				);

	// 다국어메세지파일 목록을 추가할 경우 기본경로
	private static final String BASE_PREFIX = "classpath:/i18n/";

	// 추가할 다국어메세지파일명 목록
	@Value("${cmmn-properties.i18n-message.file-names: }")
	String[] msgSourceBaseNames;


	@Bean
	MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		List<String> baseNames = makeBaseNamesForMessageSource();
		messageSource.addBasenames(baseNames.toArray(new String[0]));
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(true);

		MessageI18nUtils.setMessageSource(messageSource);

		return messageSource;
	}

	@Bean
	MessageSourceAccessor messageSourceAccessor() throws IOException {
		return new MessageSourceAccessor(messageSource());
	}

	private List<String> makeBaseNamesForMessageSource(){
		List<String> baseNameFileList = Arrays.asList(msgSourceBaseNames).stream()
											.filter(StringUtils::isNotBlank)
											.map(s -> BASE_PREFIX + s).collect(Collectors.toList());
		List<String> baseNames = Stream.concat(CORE_BASE_NAMES.stream(), baseNameFileList.stream())
									.collect(Collectors.toList());
		log.debug(">>> baseNames of MessageConfig:{}", baseNames);
		return baseNames;
	}
}
