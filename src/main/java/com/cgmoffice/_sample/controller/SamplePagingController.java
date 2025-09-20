package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice._sample.dto.PagingTestDto;
import com.cgmoffice._sample.dto.Test01Dto;
import com.cgmoffice._sample.service.SamplePagingService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * paging 모듈 동작 테스트
 */
@RestController
@RequestMapping("/api/sample/paging")
@RequiredArgsConstructor
@Slf4j
public class SamplePagingController {

	private final SamplePagingService testPagingService;

    @GetMapping("getList")
    public CmmnMap getList(@ModelAttribute PagingTestDto dto,  PageConfig pageConfig) {

    	PageList<Test01Dto> pageList = testPagingService.getList(dto, pageConfig);

    	return new CmmnMap()
    			.put("list", pageList)
    			.put("pagingInfo", pageList.getPaginator())
    			;
    }
}
