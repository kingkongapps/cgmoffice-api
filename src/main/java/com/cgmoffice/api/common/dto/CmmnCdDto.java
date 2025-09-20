package com.cgmoffice.api.common.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CmmnCdDto {

	@NotBlank
	String grpCd;

	String grpCdNm;

	@NotBlank
	String cd;

	String cdNm;

	String delYn;

	String useYn;

	String cdDesc;

	@Default
	int sortNo = -1;

	String cdVal1;
	String cdVal2;
	String cdVal3;

	@Default
	int levSeq = -1;

	String crtDtm;
	String mdfDtm;
	String crtr;
	String amdr;
}
