package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.SearchSysMsgDto;
import com.cgmoffice.api.sys.dto.SysMsgDto;
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
public class SysMsgMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//메시지코드 목록 조회
	public PageList<SysMsgDto> getListPage(SearchSysMsgDto dto, PageConfig pageConfig){
		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setMsgNm(CoreUtils.appendEscape(dto.getMsgNm(), escapeStrList, dto.getDatabaseId()));
		dto.setMsgType(CoreUtils.appendEscape(dto.getMsgType(), escapeStrList, dto.getDatabaseId()));

		PageList<SysMsgDto> list = appDao.selectListPage("api.sys.SysMsgMng.getListPage_TB_MSG", dto,pageConfig);
		return list;
	}

	//메시지코드 삭제
	public void deleteList(List<SysMsgDto> dtoList) {
		dtoList.forEach(dto -> deleteSysMsg(dto));
	}

	public void deleteSysMsg(SysMsgDto dto) {
		//삭제여부값 업데이트
		appDao.update("api.sys.SysMsgMng.delete_TB_MSG", dto);
	}

	//메시지코드 신규등록
	public void insertList(SysMsgDto dto) {
		appDao.insert("api.sys.SysMsgMng.insert_TB_MSG", dto);
	}

	//메시지코드 수정
	public void updateList(SysMsgDto dto) {
		appDao.update("api.sys.SysMsgMng.update_TB_MSG", dto);
	}

	//메시지코드 아이디 중복확인(중복되는게 있는 경우 "N" / 기존에는 있었으나 삭제 된 경우 "D" / 사용 가능 한 경우 "Y")
	public String selectMsgIdYn(SysMsgDto dto) {

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		return appDao.selectOne("api.sys.SysMsgMng.selectMsgIdYn_TB_MSG", dto);
	}



}
