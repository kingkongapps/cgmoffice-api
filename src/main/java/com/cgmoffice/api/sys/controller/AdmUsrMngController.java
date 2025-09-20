package com.cgmoffice.api.sys.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.AdmUsrDto;
import com.cgmoffice.api.sys.dto.SearchAdmUsrDto;
import com.cgmoffice.api.sys.service.AdmUsrMngService;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/AdmUsrMng")
@RequiredArgsConstructor
@Slf4j
public class AdmUsrMngController {

	private final AdmUsrMngService admUsrMngService;
	private final PasswordEncoder passwordEncoder;

	//관리자 목록 조회
	@PostMapping("getList")
	public List<AdmUsrDto> getListPage(@RequestBody SearchAdmUsrDto dto) {

		List<AdmUsrDto> list = admUsrMngService.getList(dto);

		return list;
	}


	//관리자 아이디 중복확인
	@PostMapping("selectMemIdYn")
	public String selectMemIdYn(@RequestBody AdmUsrDto dto) {

		String memIdYn = admUsrMngService.selectMemIdYn( dto);

		return memIdYn;
	}


	//관리자 신규 등록
	@PostMapping("insertList")
	public void insertList(@RequestBody AdmUsrDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		//초기화비번으로 암호화 셋팅
		String rstPwd = "1234";
		dtoList.setPasswd(passwordEncoder.encode(rstPwd));

		admUsrMngService.insertList(dtoList);
	}


	//관리자 수정
	@PostMapping("updateList")
	public void updateList(@RequestBody AdmUsrDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		admUsrMngService.updateList(dtoList);
	}

	//관리자 비밀번호 초기화
	@PostMapping("updateRstPwd")
	public void updateRstPwd(@RequestBody AdmUsrDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);
		dtoList.setFindAcYn("Y"); //계정찾기여부

		//초기화비번으로 암호화 셋팅
		String rstPwd = "1234";
		dtoList.setPasswd(passwordEncoder.encode(rstPwd));

		admUsrMngService.updateList(dtoList);
	}


	//관리자 목록 조회
	@PostMapping("searchAuthCd")
	public List<AdmUsrDto> searchAuthCd(@RequestBody AdmUsrDto dto) {

		List<AdmUsrDto> list = admUsrMngService.searchAuthCd(dto);

		return list;
	}






}
