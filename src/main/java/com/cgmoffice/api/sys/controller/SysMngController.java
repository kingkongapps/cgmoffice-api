package com.cgmoffice.api.sys.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.AuthDto;
import com.cgmoffice.api.sys.dto.SearchSysMngDto;
import com.cgmoffice.api.sys.dto.SysMngDto;
import com.cgmoffice.api.sys.service.SysMngService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/SysMng")
@RequiredArgsConstructor
@Slf4j
public class SysMngController {

	private final SysMngService sysMngService;

	//메뉴권한관리 화면 진입 시 초기 메뉴 목록 조회
	@PostMapping("getList")
	public SysMngDto getListPage(@RequestBody SearchSysMngDto dto) {

		SysMngDto sysMngDto = sysMngService.getList(dto);

		return sysMngDto;
	}

	//권한 저장
	@PostMapping("saveAuth")
	public SysMngDto saveAuth(@RequestBody List<AuthDto> dtolist) {

		return sysMngService.saveAuth(dtolist);
	}

	//신규 권한코드 조회
	@PostMapping("getNewAuthCd")
	public AuthDto getNewAuthCd(@RequestBody AuthDto dto) {

		return sysMngService.getNewAuthCd(dto);
	}
}
