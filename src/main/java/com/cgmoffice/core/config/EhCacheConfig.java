package com.cgmoffice.core.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.JCacheManagerFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cgmoffice.core.properties.CmmnProperties;

import lombok.RequiredArgsConstructor;

/**
 * EhCache 설정
 */
//@EnableCaching
//@Configuration
//@RequiredArgsConstructor
public class EhCacheConfig {

//	private final JsolProperties cmmnProperties;
//	private final ApplicationContext context;
//
//	@Bean
//	JCacheCacheManager ehCacheManager() throws Exception {
//		JCacheCacheManager cacheCacheManager = new JCacheCacheManager();
//		cacheCacheManager.setCacheManager(jCacheManagerFactoryBean().getObject());
//		return cacheCacheManager;
//	}
//
//	@Bean
//	JCacheManagerFactoryBean jCacheManagerFactoryBean() throws Exception {
//		JCacheManagerFactoryBean factoryBean = new JCacheManagerFactoryBean();
//		factoryBean.setCacheManagerUri(context.getResource(cmmnProperties.getEhcacheConfig()).getURI());
//		return factoryBean;
//	}
}
