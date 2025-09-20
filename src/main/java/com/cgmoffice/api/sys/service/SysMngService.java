package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.AuthDto;
import com.cgmoffice.api.sys.dto.AuthGrpDto;
import com.cgmoffice.api.sys.dto.MenuDto;
import com.cgmoffice.api.sys.dto.SearchSysMngDto;
import com.cgmoffice.api.sys.dto.SysMngDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SysMngService {

	private final AppDao appDao;

	//그룹코드 목록조회
	public SysMngDto getList(SearchSysMngDto dto){

		List<MenuDto> menulist = appDao.selectList("api.sys.SysMng.getList_TB_MENU", dto);			//메뉴정보조회
		List<AuthGrpDto> authGrpList = appDao.selectList("api.sys.SysMng.getList_TB_DTL_CD", dto);	//권한그룹정보조회
		List<AuthDto> authList = appDao.selectList("api.sys.SysMng.getList_TB_AUTH", dto);			//권한정보조회

		SysMngDto sysMngDto = new SysMngDto();
		sysMngDto.setMenuList(menulist);
		sysMngDto.setAuthGrpList(authGrpList);
		sysMngDto.setAuthList(authList);

		return sysMngDto;
	}

	//권한 저장
	public SysMngDto saveAuth(List<AuthDto> dtolist) {

		SysMngDto rst = new SysMngDto();

		String newYn = dtolist.get(0).getNewYn(); //신규여부

		//신규등록인 경우
		if("Y".equals(newYn)) {
			int cnt = appDao.selectOne("api.sys.SysMng.getCount_TB_AUTH", dtolist.get(0));

			//권한그룹코드 내 권한코드 중복확인
			if(0 < cnt) {
				rst.setReturnMsg("해당 권한그룹에 이미 존재하는 권한코드입니다.");
				return rst;
			}
		}

		String memId = RequestUtils.getUser().getMemId(); // 등록/수정자

		//기존 권한 삭게
		appDao.update("api.sys.SysMng.delete_TB_AUTH", dtolist.get(0));

		for(AuthDto dto : dtolist) {
			dto.setUser(memId);
			// 권한 등록
			appDao.insert("api.sys.SysMng.insert_TB_AUTH", dto);
		}

		rst.setReturnMsg("저장되었습니다.");

		return rst;
	}

	//신규 권한코드 조회
	public AuthDto getNewAuthCd(AuthDto dto) {

		return appDao.selectOne("api.sys.SysMng.getNewAuthCd", dto);
	}

}
