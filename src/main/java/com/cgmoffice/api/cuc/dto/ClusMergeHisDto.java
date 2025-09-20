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
public class ClusMergeHisDto {
	String rcmYmd;				// 합본생성일자
	String cmpnyNm;				// 보험회사
	String mxtrClusCd;			// 조합약관코드
	String contYmd;				// 계약일자
	String inskndCd;			// 보종코드
	String clusNm;				// 약관명 (상품명)
	String indvClusMxtrId;		// 개별약관조합ID
	String indvClusMergeaddId;	// 개별약관합본첨부ID
	String crtDtm;				// 생성일자
}

