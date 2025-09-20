package com.cgmoffice.api.cuc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.cuc.dto.ClusMergeHisDto;
import com.cgmoffice.api.cuc.dto.ClusMergeTxtHisDto;
import com.cgmoffice.api.cuc.dto.ClusResHisDtlDto;
import com.cgmoffice.api.cuc.dto.ClusResHisDto;
import com.cgmoffice.api.cuc.dto.SearchClusMergeHisDto;
import com.cgmoffice.api.cuc.dto.SearchClusMergeTxtHisDto;
import com.cgmoffice.api.cuc.dto.SearchClusResHisDtlDto;
import com.cgmoffice.api.cuc.dto.SearchClusResHisDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreStringUtils;
import com.cgmoffice.core.utils.CoreUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClusCdService {

	private final AppDao appDao;

	private final CmmnProperties cmmnProperties;

	//약관번호수신내역 조회
	public PageList<ClusResHisDto> selectClusResHisList(SearchClusResHisDto dto, PageConfig pageConfig){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setCmpnyNm(CoreUtils.appendEscape(dto.getCmpnyNm(), escapeStrList, dto.getDatabaseId()));

		PageList<ClusResHisDto> list = appDao.selectListPage("api.cuc.clusCd.selectClusResHisList_TB_INDV_CLUS_RCV_MST", dto, pageConfig);

		for(int i = 0; i < list.size(); i++) {
			ClusResHisDto obj = list.get(i);

			String mxtrClusCd = obj.getMxtrClusCd();
			String[] splitMxtrClusCd = mxtrClusCd.split("\\-");
			String hex = "";

			if(splitMxtrClusCd.length > 1) {
				hex = splitMxtrClusCd[1];
				list.get(i).setMxtrClusCdBinToNum(splitMxtrClusCd[0].concat("-").concat(CoreStringUtils.hexToBin(hex)));
			}
		}

		return list;
	}

	//약관번호수신내역 상세조회
	public List<ClusResHisDtlDto> selectClusResHisDtlList(SearchClusResHisDtlDto dto){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());
		List<ClusResHisDtlDto> list = appDao.selectList("api.cuc.clusCd.selectClusResHisDtlList_TB_INDV_CLUS_RCV_DTL", dto);
		return list;
	}

	//약관내보내기이력 조회
	public PageList<ClusMergeHisDto> selectClusMergeHisList(SearchClusMergeHisDto dto, PageConfig pageConfig){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setCmpnyNm(CoreUtils.appendEscape(dto.getCmpnyNm(), escapeStrList, dto.getDatabaseId()));
		dto.setClusNm(CoreUtils.appendEscape(dto.getClusNm(), escapeStrList, dto.getDatabaseId()));

		PageList<ClusMergeHisDto> list = appDao.selectListPage("api.cuc.clusCd.selectClusMergeHisListTB_INDV_CLUS_MERGEADD_INF", dto, pageConfig);
		return list;
	}

	//약관텍스트정보내역 조회
	public PageList<ClusMergeTxtHisDto> selectClusMergeTxtHisList(SearchClusMergeTxtHisDto dto, PageConfig pageConfig){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());
		PageList<ClusMergeTxtHisDto> list = appDao.selectListPage("api.cuc.clusCd.selectClusMergeTxtHisListTB_INDV_CLUS_MERGE_INF", dto, pageConfig);
		return list;
	}

	//약관텍스트정보내역 상세 조회 (미리보기 CLOB 필드만)
	public String selectClusMergeTxt(ClusMergeTxtHisDto dto){
		return appDao.selectOne("api.cuc.clusCd.selectClusMergeTxtHisListTB_INDV_CLUS_MERGE_INF_TXT_TEXT", dto);
	}


}
