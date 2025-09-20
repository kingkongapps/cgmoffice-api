package com.cgmoffice.api.sys.service;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.ErrLogHistDto;
import com.cgmoffice.api.sys.dto.SearchErrLogHistDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrLogHistMngService {

	private final AppDao appDao;

	//목록 조회
	public PageList<ErrLogHistDto> getListPage(SearchErrLogHistDto dto, PageConfig pageConfig){
		PageList<ErrLogHistDto> list = appDao.selectListPage("api.sys.ErrLogHistMng.getListPage_TB_SYS_ERRLOG_LST", dto,pageConfig);
		return list;
	}

}
