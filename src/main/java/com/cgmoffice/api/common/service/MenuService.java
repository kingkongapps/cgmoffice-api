package com.cgmoffice.api.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.common.dto.AuthMenuDto;
import com.cgmoffice.api.common.dto.MenuDto;
import com.cgmoffice.core.dao.AppDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
	private final AppDao appDao;

	public List<MenuDto> getAllMenuList() {
		return appDao.selectList("api.common.menu.getAllMenuList");
	}

	public List<AuthMenuDto> getAuthMenuList(String authCd) {
		return appDao.selectList("api.common.menu.getAuthMenuList", authCd);
	}
}
