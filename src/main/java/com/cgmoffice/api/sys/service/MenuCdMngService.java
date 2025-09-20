package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.MenuCdDto;
import com.cgmoffice.api.sys.dto.SearchMenuCdDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuCdMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//목록 조회
	public List<MenuCdDto> getListPage(SearchMenuCdDto dto){
		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setMenuCd(CoreUtils.appendEscape(dto.getMenuCd(), escapeStrList, dto.getDatabaseId()));
		dto.setMenuNm(CoreUtils.appendEscape(dto.getMenuNm(), escapeStrList, dto.getDatabaseId()));

		List<MenuCdDto> list = appDao.selectList("api.sys.MenuMng.getListPage_TB_MENU", dto);
		return list;
	}

	//삭제
	public void deleteLsit(MenuCdDto dto) {
		appDao.delete("api.sys.MenuMng.delete_TB_MENU", dto);
	}

	//메뉴 신규등록
	public void insertList(MenuCdDto dto) {

		//정렬순서 중복확인
		int sortCnt = appDao.selectOne("api.sys.MenuMng.selectCount_TB_MENU", dto);

		//정렬할 목록 조회
		if(sortCnt == 0) {
			//바로 저장
			appDao.insert("api.sys.MenuMng.insert_TB_MENU", dto);
			return;
		}else {
			String maxNum = appDao.selectOne("selectMaxSortNo_TB_MENU", dto);
			dto.setOldSortNo(Integer.parseInt(maxNum));
			appDao.update("api.sys.MenuMng.updatePlusSortNo_TB_MENU", dto);
		}

		appDao.insert("api.sys.MenuMng.insert_TB_MENU", dto);


	}

	//메뉴 수정
	public void updateList(MenuCdDto dto) {

		int oldSortNo = dto.getOldSortNo();
		int sortNo = dto.getSortNo();

		//저장된것과 정렬순서가 동일한 경우
		if(sortNo == oldSortNo) {
			//바로 업데이트
			appDao.update("api.sys.MenuMng.update_TB_MENU", dto);
			return;
		}

		//정렬순서 중복확인
		int sortCnt = appDao.selectOne("api.sys.MenuMng.selectCount_TB_MENU", dto);

		//중복이 없는 경우
		if(sortCnt == 0) {
			//바로 업데이트
			appDao.update("api.sys.MenuMng.update_TB_MENU", dto);
			return;
		}

		String maxNum = appDao.selectOne("selectMaxSortNo_TB_MENU", dto);

		//정렬순서 업데이트
		if(sortNo < oldSortNo) {
			//+
			dto.setOldSortNo(Integer.parseInt(maxNum));
			appDao.update("api.sys.MenuMng.updatePlusSortNo_TB_MENU", dto);
		}else if(sortNo > oldSortNo) {
			//-
			appDao.update("api.sys.MenuMng.updateMinusSortNo_TB_MENU", dto);
		}

		appDao.update("api.sys.MenuMng.update_TB_MENU", dto);

	}

	//메뉴코드 중복확인
	public String selectMenuCdYn(MenuCdDto dto) {

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String menuCdYn = appDao.selectOne("api.sys.MenuMng.selectMenuCdYn_TB_MENU", dto);
		return menuCdYn;
	}

	//상위코드 확인
	public List<MenuCdDto> selectUpprMenu(MenuCdDto dto) {
		List<MenuCdDto> upprMenuList = appDao.selectList("api.sys.MenuMng.selectUpprMenu_TB_MENU", dto);
		return upprMenuList;
	}

}
