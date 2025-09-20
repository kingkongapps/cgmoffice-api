package com.cgmoffice.api.sys.dto;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchMenuCdDto {

	@Default
	String menuLev = ""; //메뉴레벨

	@Default
	String menuNm = ""; //메뉴명

	@Default
	String upprMenuCd = ""; //상위메뉴

	@Default
	String menuCd = ""; //메뉴코드

	String databaseId; //디비종류

	PageConfig pageConfig;

}
