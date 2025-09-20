package com.cgmoffice.api.sys.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.LgnAuthHistDto;
import com.cgmoffice.api.sys.dto.LgnAuthHistListDto;
import com.cgmoffice.api.sys.dto.SearchLgnAuthHistDto;
import com.cgmoffice.api.sys.service.LgnAuthHistMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/lgnAuthHistMng")
@RequiredArgsConstructor
@Slf4j
public class LgnAuthHistMngController {

	private final LgnAuthHistMngService lgnAuthHistMngService;

	@PostMapping("getListPage")
	public LgnAuthHistListDto getListPage(@RequestBody SearchLgnAuthHistDto dto){

		PageConfig pageConfig = dto.getPageConfig();

		PageList<LgnAuthHistDto> pageList = lgnAuthHistMngService.getListPage(dto,pageConfig);

		return LgnAuthHistListDto.builder()
			.list(pageList)
			.paginator(pageList.getPaginator())
			.build();

	}




}
