package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.DtlCdDto;
import com.cgmoffice.api.sys.dto.SearchDtlCdDto;
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
public class DtlCdMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	//상세코드 목록조회
	public PageList<DtlCdDto> getListPage(SearchDtlCdDto dto, PageConfig pageConfig){

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setCd(CoreUtils.appendEscape(dto.getCd(), escapeStrList, dto.getDatabaseId()));
		dto.setGrpCdNm(CoreUtils.appendEscape(dto.getGrpCdNm(), escapeStrList, dto.getDatabaseId()));

		PageList<DtlCdDto> list = appDao.selectListPage("api.sys.DtlCdMng.getListPage_TB_DTL_CD", dto,pageConfig);
		return list;
	}

	//상세코드 목록조회(페이징 미적용)
	public List<DtlCdDto> getList(SearchDtlCdDto dto){
		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		List<DtlCdDto> list = appDao.selectList("api.sys.DtlCdMng.getListPage_TB_DTL_CD", dto);
		return list;
	}

	//상세코드 삭제
	public void deleteList(List<DtlCdDto> dtoList) {
		dtoList.forEach(dto -> deleteDtlCd(dto));
	}

	public void deleteDtlCd(DtlCdDto dto) {
		//삭제여부값 업데이트
		appDao.delete("api.sys.DtlCdMng.delete_TB_DTL_CD", dto);
	}

	//상세코드 신규등록
	public void insertList(DtlCdDto dto) {
		appDao.insert("api.sys.DtlCdMng.insert_TB_DTL_CD", dto);
	}

	//상세코드 수정
	public void updateList(DtlCdDto dto) {
		appDao.update("api.sys.DtlCdMng.update_TB_DTL_CD", dto);
	}

	//상세코드 코드값 중복확인
	public String selectCdYn(DtlCdDto dto) {

		//디비종류 셋팅
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String cdYn = appDao.selectOne("api.sys.DtlCdMng.selectCdYn_TB_DTL_CD", dto);
		return cdYn;
	}



}
