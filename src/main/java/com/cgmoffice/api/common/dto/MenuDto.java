package com.cgmoffice.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Builder @ToString
@AllArgsConstructor
@NoArgsConstructor
public class MenuDto {

	String menuCd;
	String menuTypCd;
	String menuNm;
	String upprMenuCd;

	String selectYn;
	String insertYn;
	String updateYn;
	String deleteYn;
	String excelYn;

	int menuLev;
	int sortNo;

	@Default
	String icon = "";
}
