package com.cgmoffice._sample.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice._sample.dto.SampleFileDto;
import com.cgmoffice._sample.dto.TestFileUpDto;
import com.cgmoffice._sample.service.SampleFileMngService;
import com.cgmoffice.core.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/fileMng")
@RequiredArgsConstructor
@Slf4j
public class SampleFileMngController {

	private final SampleFileMngService sampleFileMngService;

	@GetMapping("/getList")
    public List<SampleFileDto> getList(){
    	return sampleFileMngService.getList();
    }

	@PostMapping("/fileUp")
    public void fileUp(
    		@ModelAttribute TestFileUpDto testFileUpDto
    		){
		log.debug(">>> fileUp testFileUpDto: {}", JsonUtils.toJsonStr(testFileUpDto));
		
    	sampleFileMngService.fileUp(testFileUpDto);
    }

	@DeleteMapping("/delete")
	public void delete(@RequestParam String idx) {
		sampleFileMngService.delete(idx);
	}
}
