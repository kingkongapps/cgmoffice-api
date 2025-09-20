package com.cgmoffice.api.cnt.service;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.cnt.dto.MnClusMppgDto;
import com.cgmoffice.api.cnt.dto.SearchMnClusMppgDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.properties.CmmnProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//상품 약관 항목 조회
	public PageList<MnClusMppgDto> selectMnClusMppgList(SearchMnClusMppgDto dto, PageConfig pageConfig){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());
		PageList<MnClusMppgDto> list = appDao.selectListPage("api.cnt.pdfMng.selectMnClusMppgList_TB_CLUS_ITM_SET_MST", dto, pageConfig);
		return list;
	}

}
