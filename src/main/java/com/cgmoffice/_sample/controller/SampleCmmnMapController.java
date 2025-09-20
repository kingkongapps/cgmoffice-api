package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CmmnMap 파라메타 동작 테스트
 */
@RestController
@RequestMapping("/api/sample/cmmnMap")
@RequiredArgsConstructor
@Slf4j
public class SampleCmmnMapController {

    @PostMapping
    public CmmnMap post(CmmnMap params) {

    	log.debug(">>> post params : {}", params.toString());

        return params;
    }

    @GetMapping
    public CmmnMap get(CmmnMap params) {

    	log.debug(">>> get params : {}", params.toString());

        return params;
    }
}
