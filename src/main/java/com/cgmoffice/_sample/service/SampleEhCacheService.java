package com.cgmoffice._sample.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Service
//@RequiredArgsConstructor
//@Slf4j
public class SampleEhCacheService {

//	int count = 1;
//
//	@Cacheable(value = "cachetest01")
//	public int getCache() {
//
//		return count++;
//	}
//
//	@CacheEvict(value = "cachetest01")
//	public void cleanCache() {}
//
//	@Cacheable(value = "cachetest02", key="#keyarg")
//	public String getMinkam(String keyarg) {
//		log.info("###>>> getMinkam01 call !!! ");
//		return (String) RequestUtils.getAttribute(keyarg);
//	}
}
