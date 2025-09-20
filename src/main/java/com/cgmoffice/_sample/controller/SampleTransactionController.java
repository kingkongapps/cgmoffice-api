package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice._sample.dto.Test01Dto;
import com.cgmoffice._sample.service.SampleTransaction01Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/transaction")
@RequiredArgsConstructor
@Slf4j
public class SampleTransactionController {

	private final SampleTransaction01Service sampleTransaction01Service;

    @GetMapping("getInfo")
    public Test01Dto getInfo() {
    	return sampleTransaction01Service.getInfo();
    }

    @GetMapping("reset")
    public void reset() {
    	sampleTransaction01Service.reset();
    }

    @PatchMapping("test01")
    public void test01(@RequestParam int age) {
    	sampleTransaction01Service.test01(age);
    }

    @PatchMapping("test02")
    public void test02(@RequestParam int age) {
    	sampleTransaction01Service.test02(age);
    }

    @PatchMapping("test03")
    public void test03(@RequestParam int age) {
    	sampleTransaction01Service.test03(age);
    }

    @PatchMapping("test04")
    public void test04(@RequestParam int age) {
    	sampleTransaction01Service.test04(age);
    }

    @PatchMapping("test05")
    public void test05(@RequestParam int age) {
    	sampleTransaction01Service.test05(age);
    }

}
