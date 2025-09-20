package com.cgmoffice.api.sys.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.PasswdChgReqDto;
import com.cgmoffice.api.sys.dto.PasswdChgResDto;
import com.cgmoffice.api.sys.service.PasswdChgService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/PasswdChg")
@RequiredArgsConstructor
@Slf4j
public class PasswdChgController {

	private final PasswdChgService passwdChgService;

	//비밀번호 변경
	@PostMapping("updPasswd")
	public PasswdChgResDto updPasswd(@RequestBody PasswdChgReqDto dto) {

		return passwdChgService.updPasswd(dto);
	}

}
