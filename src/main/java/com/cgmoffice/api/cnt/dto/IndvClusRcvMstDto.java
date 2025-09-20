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
public class IndvClusRcvMstDto {

	// 개별약관조합ID
	String indvClusMxtrId;

	// 조합약관코드
	String mxtrClusCd;

	// 회사코드
	String cmpnyCd;

	// 발송URL
	String sndurl;

	// 계약일자
	String contYmd;

	// 생성자
	String crtr;

	// 수정자
	String updusr;

	// dbId
	String dbDriverId;

}
