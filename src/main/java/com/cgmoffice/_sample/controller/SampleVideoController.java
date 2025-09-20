package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/fileMng")
@RequiredArgsConstructor
@Slf4j
public class SampleVideoController {

	private final VideoService utilsVideoService;

	@PostMapping("/videoUp")
    public FileInfoDto videoUp(
    		@RequestPart MultipartFile videoFile,
    		@RequestPart MultipartFile thumnailImg
    		){

		String uploadRelativePath = "/sample";
		FileInfoDto dto = utilsVideoService.uploadVideo(videoFile, thumnailImg, uploadRelativePath);

    	return dto;
    }

}
