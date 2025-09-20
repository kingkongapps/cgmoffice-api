package com.cgmoffice.api.sys.dto;

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
public class SearchDtlCdDto {

	@Default
	String grpCdNm = ""; //그룹코드명

	@Default
	String cd = ""; //코드

	String databaseId; //디비종류

	PageConfig pageConfig;

}
