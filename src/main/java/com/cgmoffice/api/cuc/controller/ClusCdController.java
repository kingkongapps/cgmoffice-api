package com.cgmoffice.api.cuc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.cuc.dto.ClusResHisDto;
import com.cgmoffice.api.cuc.dto.ClusResHisListDto;
import com.cgmoffice.api.cuc.dto.SearchClusMergeHisDto;
import com.cgmoffice.api.cuc.dto.SearchClusMergeTxtHisDto;
import com.cgmoffice.api.cuc.dto.ClusMergeHisDto;
import com.cgmoffice.api.cuc.dto.ClusMergeHisListDto;
import com.cgmoffice.api.cuc.dto.ClusMergeTxtHisDto;
import com.cgmoffice.api.cuc.dto.ClusMergeTxtHisListDto;
import com.cgmoffice.api.cuc.dto.ClusResHisDtlDto;
import com.cgmoffice.api.cuc.dto.SearchClusResHisDto;
import com.cgmoffice.api.cuc.dto.SearchClusResHisDtlDto;
import com.cgmoffice.api.cuc.service.ClusCdService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cuc/clusCd")
@RequiredArgsConstructor
@Slf4j
public class ClusCdController {

	private final ClusCdService clusCdService;

	//약관번호수신내역 조회
	@PostMapping("clusResHisList")
	public ClusResHisListDto clusResHisList(@RequestBody SearchClusResHisDto dto){

		PageConfig pageConfig = dto.getPageConfig();

		PageList<ClusResHisDto> pageList = clusCdService.selectClusResHisList(dto, pageConfig);

		return ClusResHisListDto.builder()
			.list(pageList)
			.paginator(pageList.getPaginator())
			.build();

	}

	//약관번호수신내역 상세조회
	@PostMapping("clusResHisDtlList")
	public List<ClusResHisDtlDto> clusResHisDtlList(@RequestBody SearchClusResHisDtlDto dto){

		List<ClusResHisDtlDto> list = clusCdService.selectClusResHisDtlList(dto);

		return list;

	}

	//약관내보내기이력 조회
	@PostMapping("clusMergeHisList")
	public ClusMergeHisListDto clusMergeHisList(@RequestBody SearchClusMergeHisDto dto){

		PageConfig pageConfig = dto.getPageConfig();

		PageList<ClusMergeHisDto> pageList = clusCdService.selectClusMergeHisList(dto, pageConfig);

		return ClusMergeHisListDto.builder()
			.list(pageList)
			.paginator(pageList.getPaginator())
			.build();

	}

	//약관텍스트정보내역 조회
	@PostMapping("clusMergeTxtHisList")
	public ClusMergeTxtHisListDto clusMergeTxtHisList(@RequestBody SearchClusMergeTxtHisDto dto){

		PageConfig pageConfig = dto.getPageConfig();

		PageList<ClusMergeTxtHisDto> pageList = clusCdService.selectClusMergeTxtHisList(dto, pageConfig);

		return ClusMergeTxtHisListDto.builder()
			.list(pageList)
			.paginator(pageList.getPaginator())
			.build();

	}

	//약관텍스트정보내역 상세 조회 (미리보기 CLOB 필드만)
	@PostMapping("selectClusMergeTxt")
	public String selectClusMergeTxt(@RequestBody ClusMergeTxtHisDto dto){
		return clusCdService.selectClusMergeTxt(dto);
	}

}
