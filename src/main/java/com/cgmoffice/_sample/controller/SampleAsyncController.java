package com.cgmoffice._sample.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice._sample.service.SampleAsyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/async")
@RequiredArgsConstructor
@Slf4j
public class SampleAsyncController {

	private final SampleAsyncService sampleAsyncService;

    @GetMapping("test01")
    public void test01() {
    	sampleAsyncService.test01();

    	log.info("test01 비동기 작업 호출 완료!");
    }

    @GetMapping("test02")
    public void test02() throws InterruptedException, ExecutionException {
    	CompletableFuture<String> rslt = sampleAsyncService.test02();

    	log.info(rslt.get());
    }
}
