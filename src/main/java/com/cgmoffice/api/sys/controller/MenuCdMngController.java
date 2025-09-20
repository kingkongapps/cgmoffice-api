package com.cgmoffice.api.sys.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.MenuCdDto;
import com.cgmoffice.api.sys.dto.SearchMenuCdDto;
import com.cgmoffice.api.sys.service.MenuCdMngService;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/MenuMng")
@RequiredArgsConstructor
@Slf4j

public class MenuCdMngController {

	private final MenuCdMngService menuMngService;

	//목록 조회
	@PostMapping("getListPage")
	public List<MenuCdDto> getListPage(@RequestBody SearchMenuCdDto dto) {

		List<MenuCdDto> List = menuMngService.getListPage(dto);

		return List;
	}

	//메뉴 삭제
	@PostMapping("deleteList")
	public void deleteList(@RequestBody MenuCdDto dto) {
		menuMngService.deleteLsit(dto);
	}

	//메뉴 신규등록
	@PostMapping("insertList")
	public void insertList(@RequestBody MenuCdDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		menuMngService.insertList(dtoList);
	}

	//메뉴 수정
	@PostMapping("updateList")
	public void updateList(@RequestBody MenuCdDto  dtoList) {

		//생성자 아이디 셋팅
		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		menuMngService.updateList(dtoList);
	}

	//메뉴 아이디 중복체크
	@PostMapping("selectMenuCdYn")
	public String selectMenuCdYn(@RequestBody MenuCdDto dto) {

		String menuCdYn = menuMngService.selectMenuCdYn(dto);

		return menuCdYn;
	}

	//메뉴 아이디 중복체크
	@PostMapping("selectUpprMenu")
	public List<MenuCdDto> selectUpprMenu(@RequestBody MenuCdDto dto) {

		List<MenuCdDto> upprMenuYnList = menuMngService.selectUpprMenu(dto);

		return upprMenuYnList;
	}


}
