package com.cgmoffice.api.idc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.idc.dto.IndUnnDiffInfoDto;
import com.cgmoffice.api.idc.dto.IndUnnHisDtlDto;
import com.cgmoffice.api.idc.dto.IndUnnHisDto;
import com.cgmoffice.api.idc.dto.IndUnnHisListDto;
import com.cgmoffice.api.idc.dto.SearchIndUnnDiffInfoDto;
import com.cgmoffice.api.idc.dto.SearchIndUnnHisDtlDto;
import com.cgmoffice.api.idc.dto.SearchIndUnnHisDto;
import com.cgmoffice.api.idc.service.IndClusService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/idc/indClus")
@RequiredArgsConstructor
@Slf4j
public class IndClusController {

	private final IndClusService indClusService;

	//약관번호생성조합내역 조회
	@PostMapping("indUnnHisList")
	public IndUnnHisListDto indUnnHisList(@RequestBody SearchIndUnnHisDto dto){

		PageConfig pageConfig = dto.getPageConfig();

		PageList<IndUnnHisDto> pageList = indClusService.selectIndUnnHisList(dto, pageConfig);

		return IndUnnHisListDto.builder()
			.list(pageList)
			.paginator(pageList.getPaginator())
			.build();

	}

	//약관번호생성조합내역 상세조회
	@PostMapping("indUnnHisDtlList")
	public List<IndUnnHisDtlDto> indUnnHisDtlList(@RequestBody SearchIndUnnHisDtlDto dto){

		List<IndUnnHisDtlDto> list = indClusService.selectIndUnnHisDtlList(dto);

		return list;

	}

	//상품약관항목비교 조회
	@PostMapping("indUnnDiffInfo")
	public IndUnnDiffInfoDto indUnnDiffInfo(@RequestBody SearchIndUnnDiffInfoDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<IndUnnDiffInfoDto> pageList = indClusService.selectIndUnnDiffInfo(dto, pageConfig);

		return IndUnnDiffInfoDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

}
