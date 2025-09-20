package com.cgmoffice.api.common.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cgmoffice.api.common.dto.CmmnCdDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CmmnCodeService {
	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;

	public List<CmmnCdDto> cmmnCdAll(String grpCd) {
		CmmnMap params = new CmmnMap()
				.put("grpCd", grpCd)
				.put("databaseId", cmmnProperties.getAppDatabaseId())
				;
		List<CmmnCdDto> cmmnDtlCdList = appDao.selectList("common.code.getCmmnCd", params);
		cmmnDtlCdList.sort(Comparator.comparing(CmmnCdDto::getSortNo));

		return cmmnDtlCdList;
	}

	public List<CmmnCdDto> cmmnCd(String grpCd) {
		return cmmnCdAll(grpCd)
				.stream()
				.filter(cd -> cd.getDelYn().equals("N"))
				.collect(Collectors.toList())
				;
	}

	public PageList<CmmnCdDto> cmmnCdAllPaging(String grpCd, PageConfig pageConfig) {
		CmmnMap params = new CmmnMap()
				.put("grpCd", grpCd)
				.put("databaseId", cmmnProperties.getAppDatabaseId())
				;
		PageList<CmmnCdDto> cmmnDtlCdList = appDao.selectListPage("common.code.getCmmnCd", params, pageConfig);
		cmmnDtlCdList.sort(Comparator.comparing(CmmnCdDto::getSortNo));

		return cmmnDtlCdList;
	}

	public int chkCmmnCd(CmmnCdDto dto) {
		return appDao.selectOne("common.code.chkCmmnCd", dto);
	}

	public void saveCmmnCd(CmmnCdDto dto) {
		deleteCmmnCd(dto);
		appDao.insert("common.code.insertDtlCmmnCd", dto);
	}

	public void deleteCmmnCd(CmmnCdDto dto) {

		String memId = RequestUtils.getUser().getMemId();
		dto.setCrtr(memId);
		dto.setAmdr(memId);

		appDao.delete("common.code.deleteDtlCmmnCd", dto);
	}

	public void deleteCmmnCdList(List<CmmnCdDto> dtoList) {
		dtoList.forEach(d -> deleteCmmnCd(d));
	}

}
