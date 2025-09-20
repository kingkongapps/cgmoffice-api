package com.cgmoffice._sample.controller;

import java.util.Locale;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 다국어 설정확인 테스트
 */
@RestController
@RequestMapping("/api/sample/current-locale")
@RequiredArgsConstructor
@Slf4j
public class SampleLocaleController {

    @GetMapping
    public String getCurrentLocale(Locale locale) {
        return "Current Locale: " + locale.toString();
    }

}
