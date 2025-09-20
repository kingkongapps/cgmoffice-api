package com.cgmoffice.api.common.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/common/video")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

	private final FileService fileService;

	/**
	 * 동영상 스트리밍 출력하기
	 *
	 * @param fileNo
	 * @param rangeHeader
	 * @return
	 * @throws IOException
	 */
	@GetMapping("stream")
	public ResponseEntity<Resource> stream(
				@RequestParam String fileNo,
				@RequestHeader(value = "Range", required = false) String rangeHeader
			) throws IOException {

		FileInfoDto fileInfoDto = fileService.getFileInfo(fileNo);

		Path videoPath = Paths.get(
				new StringBuilder()
					.append(fileInfoDto.getFilePath())
					.append(File.separator)
					.append(fileInfoDto.getFileNo())
					.toString()
				);
		Resource resource = new UrlResource(videoPath.toUri());

		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}

		long fileLength = resource.contentLength();
		long rangeStart = 0;
		long rangeEnd = fileLength - 1;

		// HTTP Range 요청 처리
		if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
			String[] ranges = rangeHeader.substring(6).split("-");
			rangeStart = Long.parseLong(ranges[0]);
			if (ranges.length > 1 && !ranges[1].isEmpty()) {
				rangeEnd = Long.parseLong(ranges[1]);
			}
		}

		long contentLength = rangeEnd - rangeStart + 1;

		HttpHeaders headers = new HttpHeaders();
		headers.add(
				"Content-Range",
				new StringBuilder()
					.append("bytes ")
					.append(rangeStart)
					.append('-')
					.append(rangeEnd)
					.append('/')
					.append(fileLength)
					.toString()
				);
		headers.add("Accept-Ranges", "bytes");
		headers.add("Content-Length", String.valueOf(contentLength));

		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(resource);
	}

}
