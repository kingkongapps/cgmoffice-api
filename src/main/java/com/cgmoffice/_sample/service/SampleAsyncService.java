package com.cgmoffice._sample.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SampleAsyncService {

    @Async
    public void test01() {
        log.info("test01 비동기 작업 시작: {}", Thread.currentThread().getName());
        try {
            Thread.sleep(2000); // 작업 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("test01 비동기 작업 종료: {}", Thread.currentThread().getName());
    }

    @Async
	public CompletableFuture<String> test02() {
        try {
            log.info("test02 비동기 작업 시작: {}", Thread.currentThread().getName());
            Thread.sleep(2000); // 작업 시뮬레이션
            log.info("test02 비동기 작업 종료: {}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture("test02 비동기 작업 결과");
	}

}
