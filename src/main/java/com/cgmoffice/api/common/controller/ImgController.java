package com.cgmoffice.api.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.ImgBase64Dto;
import com.cgmoffice.api.common.service.ImgService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/common/img")
@RequiredArgsConstructor
@Slf4j
public class ImgController {

	private final ImgService imgService;

    @GetMapping("/show")
    public ResponseEntity<byte[]> show(@RequestParam String fileNo) {
    	return imgService.exportImgFile(fileNo);
    }

    @GetMapping("/base64")
    public ImgBase64Dto base64(@RequestParam String fileNo) {
    	return imgService.exportImgBase64(fileNo);
    }

}
