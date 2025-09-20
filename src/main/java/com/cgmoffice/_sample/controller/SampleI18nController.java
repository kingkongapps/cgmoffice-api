package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.MessageI18nUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 다국어 테스트
 */
@RestController
@RequestMapping("/api/sample/i18n")
@RequiredArgsConstructor
@Slf4j
public class SampleI18nController {

    @GetMapping
    public CmmnMap test01() {

    	String[] params = {"111", "222"};

    	String msg01 = MessageI18nUtils.getMessage("test.msg01");
    	String msg02 = MessageI18nUtils.getMessage("test.msg02", params);
    	String msg03 = MessageI18nUtils.getMessage("안녕하세요");
    	String msg04 = MessageI18nUtils.getExtMessage("{test.msg01}");
    	String msg05 = MessageI18nUtils.getExtMessage("test.msg01");
    	String msg06 = MessageI18nUtils.getExtMessage("안녕하세요");
    	String msg07 = MessageI18nUtils.getExtMessage("{test.msg02}", params);


    	return new CmmnMap()
        		.put("msg01", msg01)
        		.put("msg02", msg02)
        		.put("msg03", msg03)
        		.put("msg04", msg04)
        		.put("msg05", msg05)
        		.put("msg06", msg06)
        		.put("msg07", msg07)
        		;
    }

}
