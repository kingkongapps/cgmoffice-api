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
public class ClusMergeTxtHisDto {
	String rcmYmd;				// 합본생성일자
	String cmpnyNm;				// 보험회사
	String indvClusMxtrId;		// 개별약관코드
	String contYmd;				// 계약일자
	String inskndCd;			// 보종코드
	String nprdtNm;				// 약관명 (상품명)
	String indvClusMergeaddId;	// 개별약관합본첨부ID
	String txtText;				// 텍스트내용
	String crtDtm;				// 생성일자
}

