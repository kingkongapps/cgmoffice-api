package com.cgmoffice.api.sys.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.cnt.dto.PrdClusExcelDto;
import com.cgmoffice.api.cnt.dto.PrdInfoDto;
import com.cgmoffice.api.cnt.dto.PrdInfoExcelDto;
import com.cgmoffice.api.cnt.dto.PrdListDto;
import com.cgmoffice.api.cnt.dto.SearchPrdDto;
import com.cgmoffice.api.sys.service.CondPrdtMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/condPrdtMng")
@RequiredArgsConstructor
@Slf4j
public class CondPrdtMngController {

	private final CondPrdtMngService condPrdtMngService;

	//약관항목이관 - 약관항목리스트 엑셀 다운로드
	@PostMapping("/excelDownLoad_01")
	public ResponseEntity<byte[]> excelDownLoad_01(@RequestBody SearchPrdDto dto) throws IOException {
		List<PrdInfoExcelDto> pageList = condPrdtMngService.getExcelList_01(dto);

		byte[] excelFile = condPrdtMngService.downloadExcel_01(pageList);

		String encodedFileName = URLEncoder
							 		.encode("약관항목등록(이관목록).xlsx", StandardCharsets.UTF_8.toString())
							 		.replaceAll("\\+", "%20"); // 공백 처리

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", encodedFileName);

		return ResponseEntity.ok()
				.headers(headers)
				.body(excelFile);
	}

	//약관항목이관 - 약관항목구성등록 엑셀 다운로드
	@PostMapping("/excelDownLoad_02")
	public ResponseEntity<byte[]> excelDownLoad_02(@RequestBody SearchPrdDto dto) throws IOException {
		List<PrdClusExcelDto> pageList = condPrdtMngService.getExcelList_02(dto);

		byte[] excelFile = condPrdtMngService.downloadExcel_02(pageList);

		String encodedFileName = URLEncoder
							 		.encode("약관항목구성등록(이관목록).xlsx", StandardCharsets.UTF_8.toString())
							 		.replaceAll("\\+", "%20"); // 공백 처리

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", encodedFileName);

		return ResponseEntity.ok()
				.headers(headers)
				.body(excelFile);
	}

	@PostMapping("getListPage")
	public PrdListDto getListPage(@RequestBody SearchPrdDto dto) {
		PageConfig pageConfig = dto.getPageConfig();

		PageList<PrdInfoDto> pageList = condPrdtMngService.getListPage(dto, pageConfig);

		return PrdListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

	@PostMapping("getList")
	public List<PrdInfoDto> getList(@RequestBody SearchPrdDto dto) {
		return condPrdtMngService.getList(dto);
	}

	@GetMapping("getInfo")
	public PrdInfoDto getInfo(@RequestParam String prdtCd) {
		return condPrdtMngService.getPrdtInfo(prdtCd);
	}

	@PostMapping("deleteInfo")
	public void deleteInfo(@RequestBody PrdInfoDto dto) {
		condPrdtMngService.deletePrdtInfo(dto);
	}

	@PostMapping("deleteList")
	public void deleteList(@RequestBody List<PrdInfoDto>  dtoList) {
		condPrdtMngService.deletePrdtList(dtoList);
	}

	@PostMapping("saveInfo")
	public void saveInfo(@RequestBody PrdInfoDto dto) {
		// DB테이블 : TB_PRDT_INFO_MST 와 TB_PRDT_INFO_CHG_LST
		// 에 데이터를 입력한다.
		condPrdtMngService.savePrdtInfo(dto);
	}

	@PostMapping("chkExist")
	public int chkExist(@RequestBody PrdInfoDto dto) {
		return condPrdtMngService.chkExist(dto);
	}

	@PostMapping("excelUpLoad_01")
	public void excelUpLoad_01(@RequestPart MultipartFile excelFile) {
		condPrdtMngService.excelUpLoad_01(excelFile);
	}

	@PostMapping("excelUpLoad_02")
	public void excelUpLoad_02(@RequestPart MultipartFile excelFile) {
		condPrdtMngService.excelUpLoad_02(excelFile);
	}

	@GetMapping("getMaxCode")
	public String getMaxCode(@RequestParam String rquSelYn, @RequestParam String prdtCdPrefix) {
		return condPrdtMngService.getMaxCode(rquSelYn, prdtCdPrefix);
	}


}
