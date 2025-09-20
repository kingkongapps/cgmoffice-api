package com.cgmoffice.api.sys.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.ErrLogHistDto;
import com.cgmoffice.api.sys.dto.ErrLogHistListDto;
import com.cgmoffice.api.sys.dto.SearchErrLogHistDto;
import com.cgmoffice.api.sys.service.ErrLogHistMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/ErrLogHistMng")
@RequiredArgsConstructor
@Slf4j
public class ErrLogHistMngController {

	private final ErrLogHistMngService errLogHistMngService;

	//메시지코드 목록 조회
	@PostMapping("getListPage")
	public ErrLogHistListDto getListPage(@RequestBody SearchErrLogHistDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<ErrLogHistDto> pageList = errLogHistMngService.getListPage(dto, pageConfig);

		return ErrLogHistListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}


}
