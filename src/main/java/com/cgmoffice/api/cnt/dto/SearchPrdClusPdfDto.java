package com.cgmoffice.api.cnt.dto;

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
public class SearchPrdClusPdfDto {

	@Default
	String fileNm = "";

	@Default
	String rm = "";

	@Default
	String dbDriverId = "";

	@Default
	String memId = "";

	@Default
	String comCode = "";

	PageConfig pageConfig;

}
