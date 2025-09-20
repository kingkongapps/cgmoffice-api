package com.cgmoffice.api.cnt.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.cgmoffice.api.cnt.dto.PrdtClusDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrdtClusMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	public List<PrdtClusDto> getInfo(String prdtCd) {

		PrdtClusDto dto = new PrdtClusDto();

		dto.setPrdtCd(prdtCd);
		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		List<PrdtClusDto> rslt = appDao.selectList("api.cnt.prdclustmng.getInfo_TB_CLUS_ITM_SET_MST", dto);
		rslt.forEach(d -> {
			d.setFileNo(StringUtils.defaultIfEmpty(d.getFileNo(), "").trim());
			d.setFileNm(StringUtils.defaultIfEmpty(d.getFileNm(), "").trim());
		});

		return rslt;
	}

	public void save(List<PrdtClusDto> dtolist) {

		String memId = RequestUtils.getUser().getMemId();

		// 주계약코드를 추출한다.
		String prdtCd = dtolist.get(0).getPrdtCd();
		// 주계약의 모든 약관항목들을 삭제한다.
		appDao.delete("api.cnt.prdclustmng.resetPrdtList_TB_CLUS_ITM_SET_MST", prdtCd);

		dtolist.forEach(dto -> {
			dto.setCrtr(memId);
			dto.setUpdusr(memId);
			appDao.insert("api.cnt.prdclustmng.insert_TB_CLUS_ITM_SET_MST", dto);
		});
	}

	// 상품약관 주계약의 사항들을 모두 삭제플러그 셋팅을 한다.
	public void setDeleteFlagPrdtCd(String prdtCd) {
		appDao.update("api.cnt.prdclustmng.setDelFlagPrdtCd_TB_CLUS_ITM_SET_MST", prdtCd);
	}

	// 상품약관 모든 약관항목을 삭제한다.
	public void setDeleteFlagClusItmCd(String clusItmCd) {
		// 주계약에 물린것이라면 삭제한다.
		setDeleteFlagPrdtCd(clusItmCd);

		// 약관항목을 모두 삭제한다.
		appDao.update("api.cnt.prdclustmng.setDelFlagClusItmCd_TB_CLUS_ITM_SET_MST", clusItmCd);
	}

}
