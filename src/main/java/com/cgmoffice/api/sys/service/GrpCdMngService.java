package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.GrpCdDto;
import com.cgmoffice.api.sys.dto.SearchGrpCdDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrpCdMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//그룹코드 목록조회
	public List<GrpCdDto> getList(SearchGrpCdDto dto){

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setGrpCd(CoreUtils.appendEscape(dto.getGrpCd(), escapeStrList, dto.getDatabaseId()));
		dto.setGrpCdNm(CoreUtils.appendEscape(dto.getGrpCdNm(), escapeStrList, dto.getDatabaseId()));

		List<GrpCdDto> list = appDao.selectList("api.sys.GrpCdMng.getList_TB_GRP_CD", dto);
		return list;
	}


	//그룹코드 중복조회
	public String selectGrpCd(GrpCdDto dto) {

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String grpCdYn = appDao.selectOne("api.sys.GrpCdMng.selectGrpCd_TB_GRP_CD", dto);
		return grpCdYn;
	}


	//그룹코드 신규저장
	public void insertList(GrpCdDto dto) {

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		appDao.insert("api.sys.GrpCdMng.insert_TB_GRP_CD", dto);
	}

	//그룹코드 수정
	public void updateList(GrpCdDto dto) {
		appDao.update("api.sys.GrpCdMng.update_TB_GRP_CD", dto);
	}

	//그룹코드 삭제
	public void deleteList(GrpCdDto dto) {
		appDao.update("api.sys.GrpCdMng.delete_TB_GRP_CD", dto);
	}


}
