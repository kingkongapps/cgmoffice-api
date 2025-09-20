package com.cgmoffice.api.common.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/common/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

	private final FileService fileService;

    @GetMapping("/info")
    public FileInfoDto info(@RequestParam String fileNo) {
    	return fileService.getFileInfo(fileNo);
    }

	/**
	 * 첨부파일의 썸네일 가져오기
	 *
	 * @param fileNo
	 * @return
	 */
	@GetMapping("thumbnail")
	public ResponseEntity<byte[]> show(@RequestParam String fileNo) {
		FileInfoDto fileInfoDto = fileService.getFileInfo(fileNo);
		byte[] imageBytes = Base64.getDecoder().decode(fileInfoDto.getRm());
		return ResponseEntity.ok().body(imageBytes);
	}

	/**
	 * 첨부파일 다운로드하기
	 * @param fileNo
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	@PostMapping("download")
	public ResponseEntity<Resource> download(@RequestBody FileInfoDto dto) throws UnsupportedEncodingException, FileNotFoundException {
		return fileService.download(dto.getFileNo());
	}

	/**
	 * 첨부파일 다운로드하기
	 * @param fileNo
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	@GetMapping("download")
	public ResponseEntity<Resource> download(@RequestParam String fileNo) throws UnsupportedEncodingException, FileNotFoundException {
		return fileService.download(fileNo);
	}

	/**
	 * 첨부파일 다운로드하기
	 * @param fileNo
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	@GetMapping("dnfile/{fileNo}")
	public ResponseEntity<Resource> dnfile(@PathVariable String fileNo) throws UnsupportedEncodingException, FileNotFoundException {
		return fileService.download(fileNo);
	}

	@DeleteMapping("deleteDir")
	public void deleteDir(@RequestParam String targetDir) {
		fileService.deleteDir(targetDir);
	}


	@GetMapping("dnExcelTemplate")
	public ResponseEntity<Resource> dnExcelTemplate(@RequestParam String code) throws IOException {
		return fileService.dnExcelTemplate(code);
	}

}
