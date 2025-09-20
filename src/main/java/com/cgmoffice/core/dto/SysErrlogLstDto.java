package com.cgmoffice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysErrlogLstDto {

	String errLogid;
	String occurDtm;
	String sysDlngCfcd;
	String dlngTime;
	String reqr;
	String errCd;
	String errDtl;
	String menuNm;
	String methNm;
	String crtr;
	String amdr;
}
