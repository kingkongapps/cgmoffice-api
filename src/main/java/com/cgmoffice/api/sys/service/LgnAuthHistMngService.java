package com.cgmoffice.api.sys.service;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.LgnAuthHistDto;
import com.cgmoffice.api.sys.dto.SearchLgnAuthHistDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LgnAuthHistMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//로그인 이력 조회
	public PageList<LgnAuthHistDto> getListPage(SearchLgnAuthHistDto dto, PageConfig pageConfig){

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setMemId(CoreUtils.appendEscape(dto.getMemId(), escapeStrList, dto.getDatabaseId()));
		dto.setMemNm(CoreUtils.appendEscape(dto.getMemNm(), escapeStrList, dto.getDatabaseId()));

		PageList<LgnAuthHistDto> list = appDao.selectListPage("api.sys.LgnAuthHistMng.getListPage_TB_LGN_LST", dto,pageConfig);
		return list;
	}

}
