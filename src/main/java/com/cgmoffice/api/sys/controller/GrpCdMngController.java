package com.cgmoffice.api.sys.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.GrpCdDto;
import com.cgmoffice.api.sys.dto.SearchGrpCdDto;
import com.cgmoffice.api.sys.service.GrpCdMngService;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/GrpCdMng")
@RequiredArgsConstructor
@Slf4j
public class GrpCdMngController {

	private final GrpCdMngService grpCdMngService;

	//그룹코드 목록 조회
	@PostMapping("getList")
	public List<GrpCdDto> getListPage(@RequestBody SearchGrpCdDto dto) {

		List<GrpCdDto> list = grpCdMngService.getList(dto);

		return list;
	}

	//그룹코드 중복 확인
	@PostMapping("selectGrpCd")
	public String selectGrpCd(@RequestBody GrpCdDto dto) {
		String grpCdYn = grpCdMngService.selectGrpCd( dto);
		return grpCdYn;
	}


	//그룹코드 신규 저장
	@PostMapping("insertList")
	public void insertList(@RequestBody GrpCdDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		grpCdMngService.insertList(dtoList);
	}


	//그룹코드 수정
	@PostMapping("updateList")
	public void updateList(@RequestBody GrpCdDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		grpCdMngService.updateList(dtoList);
	}

	//그룹코드 삭제
	@PostMapping("deleteList")
	public void deleteList(@RequestBody GrpCdDto  dtoList) {
		grpCdMngService.deleteList(dtoList);
	}


}
