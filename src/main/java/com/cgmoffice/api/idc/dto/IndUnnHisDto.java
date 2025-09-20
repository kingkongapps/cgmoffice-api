package com.cgmoffice.api.idc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndUnnHisDto {
	String indvClusMxtrId;		// 개별약관조합ID
	String contYmd;				// 계약일자
	String apcno;				// 증권번호
	String mxtrClusCdBinToNum;	// 개별약관조합코드(이진화코드)
	String cmpnyNm;				// 보험회사
	String mxtrClusCd;			// 조합약관코드
	String inskndCd;			// 보종코드
	String prdtNm;				// 상품명
	String crtDtm;				// 생성일자
}

