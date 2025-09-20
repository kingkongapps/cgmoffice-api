package com.cgmoffice.api.sys.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.sys.dto.CmFileHistDto;
import com.cgmoffice.api.sys.dto.CmFileHistListDto;
import com.cgmoffice.api.sys.dto.SearchCmFileHistDto;
import com.cgmoffice.api.sys.service.CmFileService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/cmFile")
@RequiredArgsConstructor
@Slf4j
public class CmFileController {

	private final CmFileService cmFileService;

	//약관파일 목록 조회
	@PostMapping("getFileListPage")
	public CmFileHistListDto getFileListPage(@RequestBody SearchCmFileHistDto dto) {
		PageConfig pageConfig = dto.getPageConfig();

		PageList<CmFileHistDto> pageList = cmFileService.selectFileListPage(dto, pageConfig);

		return CmFileHistListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

	//약관파일 목록 엑셀다운로드
	@PostMapping("excelDownLoad_01")
	public ResponseEntity<byte[]> excelDownLoad_01(@RequestBody SearchCmFileHistDto dto) throws IOException {
		List<CmFileHistDto> list = cmFileService.selectFileList(dto);

		byte[] excelFile = cmFileService.downloadExcel_01(list);

		String encodedFileName = URLEncoder
							 		.encode("약관파일조회(이관목록).xlsx", StandardCharsets.UTF_8.toString())
							 		.replaceAll("\\+", "%20"); // 공백 처리

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", encodedFileName);

		return ResponseEntity.ok()
				.headers(headers)
				.body(excelFile);
	}

	//약관파일 목록 엑셀업로드
	@PostMapping("excelUpLoad_01")
	public void excelUpLoad_01(@RequestPart MultipartFile excelFile) {
		cmFileService.excelUpLoad_01(excelFile);
	}

	//약관파일 목록 파일다운로드
	@PostMapping("fileDownLoad_01")
	public ResponseEntity<Resource> fileDownLoad_01(@RequestBody SearchCmFileHistDto dto) throws IOException {
		return cmFileService.fileDownLoad_01(dto);
	}

	//약관파일 목록 파일업로드
	@PostMapping("fileUpLoad_01")
	public void fileUpLoad_01(@RequestPart MultipartFile zipFile) {
		cmFileService.fileUpLoad_01(zipFile);
	}

}
