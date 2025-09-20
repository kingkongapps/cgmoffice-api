package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.AdmUsrDto;
import com.cgmoffice.api.sys.dto.SearchAdmUsrDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdmUsrMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//관리자 목록 조회
	public List<AdmUsrDto> getList(SearchAdmUsrDto dto){
		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setMemId(CoreUtils.appendEscape(dto.getMemId(), escapeStrList, dto.getDatabaseId()));
		dto.setMemNm(CoreUtils.appendEscape(dto.getMemNm(), escapeStrList, dto.getDatabaseId()));

		List<AdmUsrDto> list = appDao.selectList("api.sys.AdmUsrMng.getList_TB_MEM", dto);
		return list;
	}

	//아이디 중복확인
	public String selectMemIdYn(AdmUsrDto dto) {

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String memIdYn = appDao.selectOne("api.sys.AdmUsrMng.selectMemIdYn_TB_MEM", dto);
		return memIdYn;
	}

	//신규등록
	public void insertList(AdmUsrDto dto) {
		appDao.insert("api.sys.AdmUsrMng.insert_TB_MEM", dto);
	}

	//수정
	public void updateList(AdmUsrDto dto) {
		appDao.update("api.sys.AdmUsrMng.update_TB_MEM", dto);
	}

	//관리자 목록 조회
	public List<AdmUsrDto> searchAuthCd(AdmUsrDto dto){
		List<AdmUsrDto> list = appDao.selectList("api.sys.AdmUsrMng.selectAuthCd_TB_AUTH", dto);
		return list;
	}



}
