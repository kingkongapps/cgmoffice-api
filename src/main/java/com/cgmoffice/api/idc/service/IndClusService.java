package com.cgmoffice.api.idc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.idc.dto.IndUnnDiffInfoDto;
import com.cgmoffice.api.idc.dto.IndUnnHisDtlDto;
import com.cgmoffice.api.idc.dto.IndUnnHisDto;
import com.cgmoffice.api.idc.dto.SearchIndUnnDiffInfoDto;
import com.cgmoffice.api.idc.dto.SearchIndUnnHisDtlDto;
import com.cgmoffice.api.idc.dto.SearchIndUnnHisDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.dao.ModuleDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndClusService {

	private final AppDao appDao;

	private final ModuleDao moduleDao;

	private final CmmnProperties cmmnProperties;

	//약관번호생성조합내역 조회
	public PageList<IndUnnHisDto> selectIndUnnHisList(SearchIndUnnHisDto dto, PageConfig pageConfig){
		dto.setDatabaseId(cmmnProperties.getModuleDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setApcno(CoreUtils.appendEscape(dto.getApcno(), escapeStrList, dto.getDatabaseId()));
		dto.setCmpnyNm(CoreUtils.appendEscape(dto.getCmpnyNm(), escapeStrList, dto.getDatabaseId()));

		PageList<IndUnnHisDto> list = moduleDao.selectListPage("api.idc.indClus.selectIndUnnHisList_TB_INDV_CLUS_CRTMXT_LST", dto, pageConfig);
		return list;
	}

	//약관번호생성조합내역 상세조회
	public List<IndUnnHisDtlDto> selectIndUnnHisDtlList(SearchIndUnnHisDtlDto dto){
		dto.setDatabaseId(cmmnProperties.getModuleDatabaseId());
		List<IndUnnHisDtlDto> list = moduleDao.selectList("api.idc.indClus.selectIndUnnHisDtlList_TB_INDV_CLUS_CRT_MPPG_LST", dto);
		return list;
	}

	//상품약관항목비교 조회
	public PageList<IndUnnDiffInfoDto> selectIndUnnDiffInfo(SearchIndUnnDiffInfoDto dto, PageConfig pageConfig) {
		dto.setDbDriverId(cmmnProperties.getModuleDatabaseId());

		//mariadb
		PageList<IndUnnDiffInfoDto> mariaRslt = moduleDao.selectListPage("api.idc.indClus.selectIndUnnDiffInfo_TB_CLUS_ITM_SET_MST", dto, pageConfig);

		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		//oracle
		PageList<IndUnnDiffInfoDto> oracleRslt = appDao.selectListPage("api.idc.indClus.selectIndUnnDiffInfo_TB_CLUS_ITM_SET_MST", dto, pageConfig);

		//값 비교
		for(int i = 0; i < mariaRslt.size(); i++) {
			mariaRslt.get(i).setIsDiff("비정상");
			if(oracleRslt.size() == 0) {
				mariaRslt.get(i).setIsDiff("값 없음");
			} else {
				for(int j = 0; i < oracleRslt.size(); j++) {
					//값 비교
					if(mariaRslt.get(i).getClusItmCd().equals(oracleRslt.get(j).getClusItmCd())) {
						mariaRslt.get(i).setIsDiff("정상");
						break;
					} else {
						mariaRslt.get(i).setIsDiff("값 상이");
					}
				}
			}
		}

		return mariaRslt;
	}


}
