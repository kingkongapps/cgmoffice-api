package com.cgmoffice.api.cuc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClusResHisDto {
	String indvClusRcvId;		// 개별약관접수ID
	String prdtCd;				// 주계약 상품코드
	String rcmYmd;				// 수신일자
	String mxtrClusCd;			// 약관번호
	String mncntrctSpccNm;		// 상품명
	String sndurl;				// 유입경로
	String cmpnyNm;				// 보험회사
	String contYmd;				// 계약일자
	String mxtrClusCdBinToNum;	// 개별약관조합코드(이진화코드)
}

