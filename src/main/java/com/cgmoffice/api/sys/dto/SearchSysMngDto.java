package com.cgmoffice.api.sys.dto;

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
public class SearchSysMngDto {

	@Default
	String upprMenuCd = ""; 	//상위메뉴코드

	@Default
	String menuCd = ""; 		//에뉴코드

	@Default
	String authGrpCd = ""; 		//권한코드

	@Default
	String authCd = ""; 		//권한그룹코드
}
