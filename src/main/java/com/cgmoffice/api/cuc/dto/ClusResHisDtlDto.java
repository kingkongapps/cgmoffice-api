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
public class ClusResHisDtlDto {
	String indvClusRcvId;		// 수신ID
	String clusItmCd;			// 약관항목코드
	String inskndCd;			// 보종코드
	String clusItmNm;			// 주계약/특약명
	String sbscrbYn;			// 주/특약가입
	String sn;					// 순번
}

