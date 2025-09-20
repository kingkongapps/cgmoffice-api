package com.cgmoffice.api.cnt.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.cnt.dto.MnClusMppgDto;
import com.cgmoffice.api.cnt.dto.MnClusMppgListDto;
import com.cgmoffice.api.cnt.dto.SearchMnClusMppgDto;
import com.cgmoffice.api.cnt.service.PdfMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cnt/pdfMng")
@RequiredArgsConstructor
@Slf4j
public class PdfMngController {

	private final PdfMngService pdfMngService;

	//상품 약관 항목 조회
	@PostMapping("mnClusMppgList")
	public MnClusMppgListDto mnClusMppgList(@RequestBody SearchMnClusMppgDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<MnClusMppgDto> pageList = pdfMngService.selectMnClusMppgList(dto, pageConfig);

		return MnClusMppgListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();

	}

}
