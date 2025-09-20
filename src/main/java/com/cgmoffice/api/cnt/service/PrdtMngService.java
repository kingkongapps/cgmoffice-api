package com.cgmoffice.api.cnt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.cnt.dto.PrdInfoDto;
import com.cgmoffice.api.cnt.dto.PrdInfoDto.PrdInfoChgLstDto;
import com.cgmoffice.api.cnt.dto.PrdtClusDto;
import com.cgmoffice.api.cnt.dto.SearchPrdDto;
import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.ExcelUtils;
import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.JsonUtils;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrdtMngService {
	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;
	private final PrdtClusMngService prdtClusMngService;
	private final FileService commonFileService;

	public PageList<PrdInfoDto> getListPage(SearchPrdDto dto, PageConfig pageConfig) {

		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setPrdtNm(CoreUtils.appendEscape(dto.getPrdtNm(), escapeStrList, dto.getDbDriverId()));

		// 상품목록 가지고 오기
		PageList<PrdInfoDto> list = appDao.selectListPage("api.cnt.prdtmng.getList_TB_PRDT_INFO_MST", dto, pageConfig);
		list.forEach(item -> {

			// dto 내의 모든 공백값은 trim 처리를 한다.
			CoreUtils.trimDtoFields(item, item.getClass());

			// 삼품별 변경이력 가지고 오기
			List<PrdInfoChgLstDto> prdInfoChgLst = getPrdInfoChgLst(item.getPrdtCd());
			item.setPrdInfoChgLst(prdInfoChgLst);
		});

		return list;
	}

	public List<PrdInfoChgLstDto> getPrdInfoChgLst(String prdtCd) {
		PrdInfoChgLstDto dto = new PrdInfoChgLstDto();

		dto.setPrdtCd(prdtCd);
		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		return appDao.selectList("api.cnt.prdtmng.getInfo_TB_PRDT_INFO_CHG_LST", dto);
	}

	public PrdInfoDto getPrdtInfo(String prdtCd) {
		PrdInfoDto dto = new PrdInfoDto();

		dto.setPrdtCd(prdtCd);
		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		return appDao.selectOne("api.cnt.prdtmng.getInfo_TB_PRDT_INFO_MST", dto);
	}

	public PrdInfoDto getPrdtInfoIgnoreDel(String prdtCd) {
		return appDao.selectOne("api.cnt.prdtmng.getInfoIgnoreDel_TB_PRDT_INFO_MST", prdtCd);
	}

	public void deletePrdtInfo(PrdInfoDto dto) {
		// 마스터정보 삭제
		appDao.update("api.cnt.prdtmng.delete_TB_PRDT_INFO_MST", dto);

		// 상품약관 모든 약관항목을 삭제플러그를 셋팅한다.
		prdtClusMngService.setDeleteFlagClusItmCd(dto.getPrdtCd());

		//약관파일변경내역 삭제 추가 25.7.21
		appDao.update("api.cnt.prdtmng.delete_TB_PRDT_INFO_CHG_LST", dto);
	}

	public void deletePrdtList(List<PrdInfoDto> dtoList) {
		dtoList.forEach(dto -> deletePrdtInfo(dto));
	}



	public void savePrdtInfo(PrdInfoDto dto) {
		String memId = RequestUtils.getUser().getMemId();
		dto.setCrtr(memId);
		dto.setUpdusr(memId);
		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		// 변경이력 추가여부
		String addChangLogYn = dto.getAddChangLogYn();
		// 신규여부
		String newYn = dto.getNewYn();

		if("Y".equals(newYn)) {
			// 마스터정보 추가
			appDao.insert("api.cnt.prdtmng.insert_TB_PRDT_INFO_MST", dto);
			// 변경이력 추가
			insertChgLst(dto);
		} else {
			// 마스터정보 수정
			appDao.update("api.cnt.prdtmng.update_TB_PRDT_INFO_MST", dto);
			if("Y".equals(addChangLogYn)) {
				// 변경이력 추가
				insertChgLst(dto);
			} else {
				// 변경이력 수정
				appDao.update("api.cnt.prdtmng.update_TB_PRDT_INFO_CHG_LST", dto);
			}
		}

		// 상품약관항목기본 테이블의 상품정보도 수정한다.
		appDao.update("api.cnt.prdtmng.update_TB_CLUS_ITM_SET_MST", dto);
	}

	private void insertChgLst(PrdInfoDto dto) {
		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());
		appDao.insert("api.cnt.prdtmng.insert_TB_PRDT_INFO_CHG_LST", dto);
	}
//	//약관파일변경이력 삭제 추가 25.7.21
//	private void deleteChgLst(PrdInfoDto dto) {
//		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());
//		appDao.delete("api.cnt.prdtmng.delete_TB_PRDT_INFO_CHG_LST", dto);
//	}

	public int chkExist(PrdInfoDto dto) {
		return appDao.selectOne("api.cnt.prdtmng.chkExist_TB_PRDT_INFO_MST", dto);
	}

	public void excelUp_01(MultipartFile excelFile) {
		List<List<String>> excelDataList = ExcelUtils.readExcel(excelFile);

		log.debug(">>> excelData: {}", JsonUtils.toJsonStr(excelDataList));
		int size = excelDataList.size();

		String prdtEndYmd = "99991231"; // 약관정보종료일자
		String memId = RequestUtils.getUser().getMemId();

		// 엑셀의 3번째 행부터 추출을 시작한다.
		for(int i=2; i < size; i++) {
			List<String> excelData = excelDataList.get(i);

			String inskndCd = excelData.get(0); // 보종코드
			String nprdtNm = excelData.get(1); // 정식상품명
			String prdtCfcd = excelData.get(2); // 상품구분
			String mnspccCfcd = excelData.get(3); // 주특약구분
			String cmpnyCode = excelData.get(4); // 회사코드
			String pmBeginYmd = excelData.get(5); // 판매개시일자
			String pmStopYmd = excelData.get(6); // 판매중지일자
			String clusItmClcd = excelData.get(7); // 약관항목분류 코드
			String prdtCd = excelData.get(8); // 약관파일명(상품코드)


			if(StringUtils.isEmpty(nprdtNm)) {
				throw new CmmnBizException("정식상품명이 비어있습니다.");
			}
			if(StringUtils.isEmpty(prdtCfcd)) {
				throw new CmmnBizException("상품구분이 비어있습니다.");
			}
			if(StringUtils.isEmpty(mnspccCfcd)) {
				throw new CmmnBizException("주툭구분이 비어있습니다.");
			}
			if(StringUtils.isEmpty(cmpnyCode)) {
				throw new CmmnBizException("회사코드가 비어있습니다.");
			}
			if(StringUtils.isEmpty(pmBeginYmd)) {
				throw new CmmnBizException("판매개시일자가 비어있습니다.");
			}
			if(StringUtils.isEmpty(clusItmClcd)) {
				throw new CmmnBizException("약관항목분류코드가 비어있습니다.");
			}
			if(StringUtils.isEmpty(prdtCd)) {
				throw new CmmnBizException("약관파일명(상품코드)가 비어있습니다.");
			}
			String prdtChgYmd = pmBeginYmd; // 약관정보변경일자

			// 기존에 상품코드명으로 약관파일명이 저장된 파일이 존재하는지를 체크한다.
			List<FileInfoDto> fileList = commonFileService.getFileInfoListByFileNm(prdtCd + ".pdf");
			String fileNo =
					fileList.size() == 0 ? "" : fileList.get(0).getFileNo();

			// 상품 마스터에 신규입력여부
			String newYn =
					getPrdtInfoIgnoreDel(prdtCd) == null ? "Y" : "N";

			Map<String, String> params = new HashMap<String, String>();
			params.put("prdtCd", prdtCd);
			params.put("prdtChgYmd", prdtChgYmd);
			int cnt = appDao.selectOne("api.cnt.prdtmng.chk_TB_PRDT_INFO_CHG_LST", params);
			String changLogYn = cnt > 0 ? "N" : "Y";

			PrdInfoDto data = PrdInfoDto.builder()
					.cmpnyCode(cmpnyCode)
					.prdtCd(prdtCd)
					.nprdtNm(nprdtNm)
					.sprdtNm("")
					.prdtCfcd(prdtCfcd)
					.pmBeginYmd(pmBeginYmd)
					.pmStopYmd(pmStopYmd)
					.prdtChgYmd(prdtChgYmd)
					.prdtEndYmd(prdtEndYmd)
					.crtr(memId)
					.updusr(memId)
					.prdtText("")
					.inskndCd(inskndCd)
					.mnspccCfcd(mnspccCfcd)
					.clusItmClcd(clusItmClcd)
					.fileNo(fileNo)
					.newYn(newYn)  // 마스터에 신규입력여부
					.addChangLogYn(changLogYn) // 변경이력은 무조건 신규추가
					.build();
			// DB테이블 : TB_PRDT_INFO_MST 와 TB_PRDT_INFO_CHG_LST
			// 에 데이터를 입력한다.
			savePrdtInfo(data);
		}
	}

	public List<PrdInfoDto> getList(SearchPrdDto dto) {
		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setPrdtNm(CoreUtils.appendEscape(dto.getPrdtNm(), escapeStrList, dto.getDbDriverId()));

		return appDao.selectList("api.cnt.prdtmng.getList_TB_PRDT_INFO_MST", dto);
	}

	// DB테이블 : TB_PRDT_INFO_MST 와 TB_PRDT_INFO_CHG_LST
	// 데이터를 입력한다.
	private void savePrdtInfo_Excel02(List<List<String>> excelDataList) {

		log.debug(">>> excelData: {}", JsonUtils.toJsonStr(excelDataList));
		int size = excelDataList.size();

		String prdtEndYmd = "99991231"; // 약관정보종료일자
		String memId = RequestUtils.getUser().getMemId();


		List<String> taskChk = new ArrayList<String>();
		// 엑셀의 3번째 행부터 추출을 시작한다.
		for(int i=2; i < size; i++) {
			List<String> excelData = excelDataList.get(i);

			String screnDispOrd = excelData.get(0); // 목차순서
			String inskndCd = excelData.get(1); // 보종코드
			String nprdtNm = excelData.get(2); // 정식상품명
			String prdtCfcd = excelData.get(3); // 상품구분
			String mnspccCfcd = excelData.get(4); // 주특약구분
			String cmpnyCode = excelData.get(5); // 회사코드
			String pmBeginYmd = excelData.get(6); // 판매개시일자
			String pmStopYmd = excelData.get(7); // 판매중지일자
			String clusItmClcd = excelData.get(8); // 약관항목분류 코드
			String clusItmCd = excelData.get(9); // 약관파일명(약관항목코드)
			String prdtCd = excelData.get(10); // 주계약상품코드

			if(StringUtils.isEmpty(nprdtNm)) {
				throw new CmmnBizException("정식상품명이 비어있습니다.");
			}
			if(StringUtils.isEmpty(prdtCfcd)) {
				throw new CmmnBizException("상품구분이 비어있습니다.");
			}
			if(StringUtils.isEmpty(mnspccCfcd)) {
				throw new CmmnBizException("주툭구분이 비어있습니다.");
			}
			if(StringUtils.isEmpty(cmpnyCode)) {
				throw new CmmnBizException("회사코드가 비어있습니다.");
			}
			if(StringUtils.isEmpty(pmBeginYmd)) {
				throw new CmmnBizException("판매개시일자가 비어있습니다.");
			}
			if(StringUtils.isEmpty(clusItmClcd)) {
				throw new CmmnBizException("약관항목분류코드가 비어있습니다.");
			}
			if(StringUtils.isEmpty(clusItmCd)) {
				throw new CmmnBizException("약관파일명(상품코드)가 비어있습니다.");
			}
			if(StringUtils.isEmpty(prdtCd)) {
				throw new CmmnBizException("주계약상품코드가 비어있습니다.");
			}
			String prdtChgYmd = pmBeginYmd; // 약관정보변경일자

			// 기존에 상품코드명으로 약관파일명이 저장된 파일이 존재하는지를 체크한다.
			List<FileInfoDto> fileList = commonFileService.getFileInfoListByFileNm(clusItmCd + ".pdf");
			String fileNo =
					fileList.size() == 0 ? "" : fileList.get(0).getFileNo();

			excelData.add(fileNo);

			if(taskChk.contains(clusItmCd)) {
				continue;
			} else {
				taskChk.add(clusItmCd);
			}

			// 상품 마스터에 신규입력여부
			String newYn =
					getPrdtInfoIgnoreDel(clusItmCd) == null ? "Y" : "N";


			Map<String, String> params = new HashMap<String, String>();
			params.put("prdtCd", clusItmCd);
			params.put("prdtChgYmd", prdtChgYmd);
			int cnt = appDao.selectOne("api.cnt.prdtmng.chk_TB_PRDT_INFO_CHG_LST", params);
			String changLogYn = cnt > 0 ? "N" : "Y";

			PrdInfoDto data = PrdInfoDto.builder()
					.cmpnyCode(cmpnyCode)
					.prdtCd(clusItmCd)
					.nprdtNm(nprdtNm)
					.sprdtNm("")
					.prdtCfcd(prdtCfcd)
					.pmBeginYmd(pmBeginYmd)
					.pmStopYmd(pmStopYmd)
					.prdtChgYmd(prdtChgYmd)
					.prdtEndYmd(prdtEndYmd)
					.crtr(memId)
					.updusr(memId)
					.prdtText("")
					.inskndCd(inskndCd)
					.mnspccCfcd(mnspccCfcd)
					.clusItmClcd(clusItmClcd)
					.fileNo(fileNo)
					.newYn(newYn)  // 마스터에 신규입력여부
					.addChangLogYn(changLogYn) // 변경이력은 무조건 신규추가
					.build();
			// DB테이블 : TB_PRDT_INFO_MST 와 TB_PRDT_INFO_CHG_LST
			// 데이터를 입력한다.
			savePrdtInfo(data);
		}
	}

	// 상품약관구성항목 데이터입력
	// 상품약관구성테이블: TB_CLUS_ITM_SET_MST
	private void saveClusItem(List<List<String>> excelDataList) {

		int size = excelDataList.size();

		String prdtCd_bak = null;

		// 기존의 주계약에 해당하는 상품약관정보를 모두 삭제한다.
		// 엑셀의 3번째 행부터 추출을 시작한다.
		for(int i=2; i < size; i++) {
			List<String> excelData = excelDataList.get(i);
			String prdtCd = excelData.get(10); // 주계약상품코드
			if(!prdtCd.equals(prdtCd_bak)) {
				// 상품약관구성항목 데이터입력
				// TB_CLUS_ITM_SET_MST 테이블의 주계약의 상품약관구성항목을 모두 삭제한다.
				// 주계약의 모든 약관항목들을 삭제한다.
				appDao.delete("api.cnt.prdclustmng.resetPrdtList_TB_CLUS_ITM_SET_MST", prdtCd);
				prdtCd_bak = prdtCd;
			}
		}


		List<String> taskChk = new ArrayList<String>();
		// 상품약관정보를 입력한다.
		List<PrdtClusDto> dtolist = new ArrayList<PrdtClusDto>();
		// 엑셀의 3번째 행부터 추출을 시작한다.

		int sn = 0;
		for(int i=2; i < size; i++) {

			List<String> excelData = excelDataList.get(i);

			String screnDispOrd = excelData.get(0); // 목차순서
			String inskndCd = excelData.get(1); // 보종코드
			String nprdtNm = excelData.get(2); // 정식상품명
			String prdtCfcd = excelData.get(3); // 상품구분
			String mnspccCfcd = excelData.get(4); // 주특약구분
			String cmpnyCode = excelData.get(5); // 회사코드
			String pmBeginYmd = excelData.get(6); // 판매개시일자
			String pmStopYmd = excelData.get(7); // 판매중지일자
			String clusItmClcd = excelData.get(8); // 약관항목분류 코드
			String clusItmCd = excelData.get(9); // 약관파일명(약관항목코드)
			String prdtCd = excelData.get(10); // 주계약상품코드
			String fileNo = excelData.get(11); // 약관파일 아이디

			if(taskChk.contains(prdtCd)) {
				sn++;
			} else {
				sn = 0;
				taskChk.add(prdtCd);
			}

			PrdtClusDto prdtClusDto = PrdtClusDto.builder()
					.clusItmCd(clusItmCd)
					.clusItmNm(nprdtNm)
					.clusItmClcd(clusItmClcd)
					.prdtCd(prdtCd)
					.screnDispOrd(sn + 1)  // 목차는 1부터 시작
					.fileNo(fileNo)
					.sn(sn)  // 약관생성순서는 주계약이 가장먼저 나오므로... 0부터 시작
					.build();
			dtolist.add(prdtClusDto);
		}

		String memId = RequestUtils.getUser().getMemId();
		dtolist.forEach(dto -> {
			dto.setCrtr(memId);
			dto.setUpdusr(memId);
			appDao.insert("api.cnt.prdclustmng.insert_TB_CLUS_ITM_SET_MST", dto);
		});
	}

	public void excelUp_02(MultipartFile excelFile) {
		List<List<String>> excelDataList = ExcelUtils.readExcel(excelFile);

		// DB테이블 : TB_PRDT_INFO_MST 와 TB_PRDT_INFO_CHG_LST
		// 데이터를 입력한다.
		savePrdtInfo_Excel02(excelDataList);

		// 상품약관구성항목 데이터입력
		// 상품약관구성테이블: TB_CLUS_ITM_SET_MST
		saveClusItem(excelDataList);

	}

	public String getMaxCode(String rquSelYn, String prdtCdPrefix) {

		CmmnMap params = new CmmnMap()
				.put("prdtCdPrefix", prdtCdPrefix)
				.put("rquSelYn", rquSelYn)
				.put("dbDriverId", cmmnProperties.getAppDatabaseId())
				;

		String maxCode = appDao.selectOne("api.cnt.prdtmng.getMaxCode_TB_CLUS_ITM_SET_MST", params);
		if(maxCode == null) {
			return "Y".equals(rquSelYn) ? "9001" : "0001";
		}

		int num = Integer.parseInt(maxCode.substring(4)) + 1;

		return StringUtils.leftPad(
				Integer.toString(num)
				,4
				,'0'
			);
	}

}
