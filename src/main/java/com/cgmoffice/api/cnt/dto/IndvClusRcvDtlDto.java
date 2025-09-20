package com.cgmoffice.api.cnt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndvClusRcvDtlDto {

	// 개별약관조합ID
	String indvClusMxtrId;

	// 약관항목코드
	String clusItmCd;

	// 약관항목명
	String clusItmNm;

	// 가입여부
	String sbscrbYn;

	// 생성자
	String crtr;

	// 수정자
	String updusr;
}
