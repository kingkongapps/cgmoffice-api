package com.cgmoffice.api.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.cnt.dto.PrdInfoDto;
import com.cgmoffice.api.cnt.dto.PrdInfoDto.PrdInfoChgLstDto;
import com.cgmoffice.api.cnt.service.PrdtMngService;
import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.api.common.service.UserService;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardMngService {

	private final AppDao appDao;
	private final PrdtMngService prdtMngService;
	private final UserService userService;
	private final CmmnProperties cmmnProperties;

	public CmmnMap getInfo() {

		// DB에서 사용자정보를 추출한다.
		UserDto userDto = userService.getUserInfo(SecurityUtil.getCurrentUsername());

		String databaseId = cmmnProperties.getAppDatabaseId();

		// 6월 이내(상반기, 전년 상반기) 또는 7월 이상(하반기, 전년 후반기)별 개별약관생성 건수
		CmmnMap params = new CmmnMap()
				.put("databaseId", databaseId)
				.put("comCode", userDto.getComCode())
				;
		List<CmmnMap> quarterDataList = appDao.selectList("api.sys.dashboardmng.getClusRcvHistMonth_TB_INDV_CLUS_RCV_MST", params);

		// 개별약관생성 총 건수
		int clusRcvTotalCnt = appDao.selectOne("api.sys.dashboardmng.getClusRcvTotalCnt_TB_INDV_CLUS_RCV_MST", params);

		// 상품등록 총 건수
		int prdtTotalCnt = appDao.selectOne("api.sys.dashboardmng.getPrdtTotalCnt_TB_PRDT_INFO_MST", params);

		// 상품등록 당월건수
		int prdtCurrMonthCnt = appDao.selectOne("api.sys.dashboardmng.getPrdtCurrMonthCnt_TB_PRDT_INFO_MST", params);

		// 약관파일등록 grid 데이터
		List<PrdInfoDto> grid01 = appDao.selectList("api.sys.dashboardmng.getGrid01_TB_PRDT_INFO_MST", params);
		grid01.forEach(item -> {
			// 상품별 변경이력 가지고 오기
			List<PrdInfoChgLstDto> prdInfoChgLst = prdtMngService.getPrdInfoChgLst(item.getPrdtCd());
			item.setPrdInfoChgLst(prdInfoChgLst);
		});

		// 상품약관등록 grid 데이터
		List<PrdInfoDto> grid02 = appDao.selectList("api.sys.dashboardmng.getGrid02_TB_PRDT_INFO_MST", params);
		grid02.forEach(item -> {
			// 상품별 변경이력 가지고 오기
			List<PrdInfoChgLstDto> prdInfoChgLst = prdtMngService.getPrdInfoChgLst(item.getPrdtCd());
			item.setPrdInfoChgLst(prdInfoChgLst);
		});

		return new CmmnMap()
				.put("clusRcvTotalCnt", clusRcvTotalCnt)  // 개별약관생성 총 건수
				.put("prdtTotalCnt", prdtTotalCnt)  // 상품등록 총 건수
				.put("prdtCurrMonthCnt", prdtCurrMonthCnt)  // 상품등록 당월건수
				.put("quarterDataList", quarterDataList)  // 6월 이내(상반기, 전년 상반기) 또는 7월 이상(하반기, 전년 후반기)별 개별약관생성 건수
				.put("grid01", grid01)  // 약관파일등록 grid 데이터
				.put("grid02", grid02)  // 상품약관등록 grid 데이터
				;
	}

}
