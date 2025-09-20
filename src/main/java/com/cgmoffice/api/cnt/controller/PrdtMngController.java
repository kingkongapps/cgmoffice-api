package com.cgmoffice.api.cnt.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.cnt.dto.PrdInfoDto;
import com.cgmoffice.api.cnt.dto.PrdListDto;
import com.cgmoffice.api.cnt.dto.SearchPrdDto;
import com.cgmoffice.api.cnt.service.PrdtMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cnt/prdtMng")
@RequiredArgsConstructor
@Slf4j
public class PrdtMngController {

	private final PrdtMngService prdtMngService;

	@PostMapping("getListPage")
	public PrdListDto getListPage(@RequestBody SearchPrdDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<PrdInfoDto> pageList = prdtMngService.getListPage(dto, pageConfig);

		return PrdListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

	@PostMapping("getList")
	public List<PrdInfoDto> getList(@RequestBody SearchPrdDto dto) {
		return prdtMngService.getList(dto);
	}

	@GetMapping("getInfo")
	public PrdInfoDto getInfo(@RequestParam String prdtCd) {
		return prdtMngService.getPrdtInfo(prdtCd);
	}

	@PostMapping("deleteInfo")
	public void deleteInfo(@RequestBody PrdInfoDto dto) {
		prdtMngService.deletePrdtInfo(dto);
	}

	@PostMapping("deleteList")
	public void deleteList(@RequestBody List<PrdInfoDto>  dtoList) {
		prdtMngService.deletePrdtList(dtoList);
	}

	@PostMapping("saveInfo")
	public void saveInfo(@RequestBody PrdInfoDto dto) {
		// DB테이블 : TB_PRDT_INFO_MST 와 TB_PRDT_INFO_CHG_LST
		// 에 데이터를 입력한다.
		prdtMngService.savePrdtInfo(dto);
	}

	@PostMapping("chkExist")
	public int chkExist(@RequestBody PrdInfoDto dto) {
		return prdtMngService.chkExist(dto);
	}

	@PostMapping("excelUp_01")
	public void excelUp_01(@RequestPart MultipartFile excelFile) {
		prdtMngService.excelUp_01(excelFile);
	}

	@PostMapping("excelUp_02")
	public void excelUp_02(@RequestPart MultipartFile excelFile) {
		prdtMngService.excelUp_02(excelFile);
	}

	@GetMapping("getMaxCode")
	public String getMaxCode(@RequestParam String rquSelYn, @RequestParam String prdtCdPrefix) {
		return prdtMngService.getMaxCode(rquSelYn, prdtCdPrefix);
	}


}
