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
public class SearchGrpCdDto {

	@Default
	String grpCdNm = ""; //그룹코드명

	@Default
	String grpCd = ""; //그룹코드

	String databaseId; //디비종류


}
