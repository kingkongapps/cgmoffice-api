package com.cgmoffice.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthMenuDto {

	String authCd;
	String menuCd;
	String selectYn;
	String insertYn;
	String updateYn;
	String deleteYn;
	String excelYn;

}
