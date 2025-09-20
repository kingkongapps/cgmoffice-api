package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice._sample.service.SampleThreadLocalService;
import com.cgmoffice._sample.util.SampleConstants;
import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/threadlocal")
@RequiredArgsConstructor
@Slf4j
public class SampleThreadLocalController {

	private final SampleThreadLocalService sampleThreadLocalService;


	@GetMapping("test01")
	public CmmnMap test01 () throws InterruptedException {

		SampleConstants.temp.set("test01 호출");

		Thread.sleep(2000);

		return sampleThreadLocalService.test01();
	}

	@GetMapping("test02")
	public CmmnMap test02 () {

		SampleConstants.temp.set("test02 호출");
		return sampleThreadLocalService.test02();
	}
}
