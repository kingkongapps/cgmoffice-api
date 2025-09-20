package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.ImgService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/imgMng")
@RequiredArgsConstructor
@Slf4j
public class SampleImgMngController {
	private final ImgService utilsImgService;

	@PostMapping("/imgUp")
    public FileInfoDto imgUp(@RequestPart MultipartFile imgFile){

		String uploadRelativePath = "/sample";
		FileInfoDto dto = utilsImgService.uploadImg(imgFile, uploadRelativePath);

    	return dto;
    }

}
