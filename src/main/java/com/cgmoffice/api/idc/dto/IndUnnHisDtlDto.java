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
public class IndUnnHisDtlDto {
	String clusItmCd;			// 약관항목코드
	String inskndCd;			// 보종코드
	String mncntrctSpccNm;		// 주계약특약명
	String sn;					// 순번
	String joinYn;				// 주/특약가입
	String sigumaCd;			// 조합코드
	String mxtrClusCdBinToNum;	// 진수
	String rowCellMergeYn;		// 셀 병합 여부 (행)
	String rowCellMergeCnt;		// 셀 병합 카운트
}

