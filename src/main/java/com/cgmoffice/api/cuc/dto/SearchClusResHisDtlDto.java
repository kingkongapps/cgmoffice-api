package com.cgmoffice.api.cuc.dto;

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
public class SearchClusResHisDtlDto {

	@Default
	String indvClusRcvId = "";

	@Default
	String prdtCd = "";

	@Default
	String databaseId = "";

	PageConfig pageConfig;

}

