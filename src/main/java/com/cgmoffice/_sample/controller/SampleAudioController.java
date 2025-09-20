package com.cgmoffice._sample.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.AudioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/audio")
@RequiredArgsConstructor
@Slf4j
public class SampleAudioController {

	private final AudioService utilsAudioService;

	@PostMapping("/audioUp")
    public FileInfoDto audioUp(
    		@RequestPart MultipartFile audioFile,
    		@RequestPart MultipartFile thumnailImg
    		){

		String uploadRelativePath = "/sample";
		FileInfoDto dto = utilsAudioService.uploadAudio(audioFile, thumnailImg, uploadRelativePath);

    	return dto;
    }

}
