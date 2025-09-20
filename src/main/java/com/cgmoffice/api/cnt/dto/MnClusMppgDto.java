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
public class MnClusMppgDto {
	String clusItmCd;			// 약관항목코드
	String clusItmClcd;			// 항목분류
	String clusItmNm;			// 약관항목명(상품약관구성)
	String inskndCd;			// 보종코드
	String prdtCd;				// 상품코드
	String sn;					// 약관생성순서
	String screnDispOrd;		// 목차순서
	String fileNo;				// 파일넘버
	String fileNm;				// 파일명
	String cmpnyCode;			// 회사코드
	String pageFld;				// 목차PDF하단페이징높이
}

