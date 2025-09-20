package com.cgmoffice._sample.service;

import org.springframework.stereotype.Service;

import com.cgmoffice._sample.dto.PagingTestDto;
import com.cgmoffice._sample.dto.Test01Dto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SamplePagingService {

	private final AppDao appDao;

	public PageList<Test01Dto> getList(PagingTestDto dto, PageConfig pageConfig) {
		return appDao.selectListPage("sample.samplePaging.getList", dto, pageConfig);
	}

}
