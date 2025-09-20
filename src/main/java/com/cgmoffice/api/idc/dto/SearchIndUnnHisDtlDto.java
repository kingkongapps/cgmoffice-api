package com.cgmoffice.api.idc.dto;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndUnnHisDtlDto {

	@Default
	String indvClusMxtrId = "";

	@Default
	String databaseId = "";

	PageConfig pageConfig;

}

