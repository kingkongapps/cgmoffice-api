package com.cgmoffice._sample.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice._sample.dto.SampleExcelDnDto;
import com.cgmoffice._sample.dto.Test01Dto;
import com.cgmoffice._sample.service.SampleExcelMngService;
import com.cgmoffice.api.common.dto.ExcelDownConfigDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/excelMng")
@RequiredArgsConstructor
@Slf4j
public class SampleExcelMngController {

	private final SampleExcelMngService sampleExcelMngService;

	@GetMapping("/download01")
    public ResponseEntity<byte[]> download01(@RequestParam String fileName) throws UnsupportedEncodingException {

    	byte[] excelFile = sampleExcelMngService.createExcel();

    	String encodedFileName = URLEncoder
					    	 		.encode(fileName, StandardCharsets.UTF_8.toString())
					    	 		.replaceAll("\\+", "%20"); // 공백 처리

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", encodedFileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }

	@PostMapping("/download02")
    public ResponseEntity<byte[]> download02(@RequestBody SampleExcelDnDto dto) throws UnsupportedEncodingException {

    	dto.setExcelDownConfig(new ExcelDownConfigDto());

    	return sampleExcelMngService.download02(dto);
    }

	@PostMapping("/upload")
    public List<List<String>> upload(
    		@RequestParam MultipartFile excelfile,
    		@ModelAttribute Test01Dto dto
    		){

		log.debug(">>> upload dto : {}", dto.toString());

    	return sampleExcelMngService.read(excelfile);
    }
}
