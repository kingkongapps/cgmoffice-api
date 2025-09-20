package com.cgmoffice.api.common.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/common/audio")
@RequiredArgsConstructor
@Slf4j
public class AudioController {

	private final FileService fileService;

	@GetMapping("stream")
	public ResponseEntity<Resource> streamAudio(@RequestParam String fileNo) throws MalformedURLException {

		FileInfoDto fileInfoDto = fileService.getFileInfo(fileNo);

		Path filePath = Paths.get(
				new StringBuilder()
				.append(fileInfoDto.getFilePath())
				.append(File.separator)
				.append(fileInfoDto.getFileNm())
				.toString()
		);
		Resource resource = new UrlResource(filePath.toUri());

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, fileInfoDto.getFileType())
				.body(resource);
	}
}
