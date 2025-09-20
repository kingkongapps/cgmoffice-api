package com.cgmoffice.api.common.controller;

import java.util.Locale;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;

/**
 * locale 변경하는 api
 */
@RestController
@RequestMapping("/api/common/changLocale")
@RequiredArgsConstructor
public class ChangeLocaleController {


    @GetMapping
    public CmmnMap get(Locale locale) {

    	return new CmmnMap()
    			.put("rslt", "Locale change Success!")
    			.put("locale", locale.toString())
    			;
    }
}
