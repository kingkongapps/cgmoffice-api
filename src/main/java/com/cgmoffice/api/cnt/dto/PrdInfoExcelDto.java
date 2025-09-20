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
public class PrdInfoExcelDto {
	String num;  //순번
	String cmpnyCode;  // 회사코드
	String mnspccCfcd;  // 주특약구분코드
	String clusItmClcd;  // 약관항목분류코드
	String prdtCd;  // 상품코드
	String sprdtNm;  // 약식상품명
	String nprdtNm;  // 정식상품명
	String prdtCfcd;  // 상품구분코드
	String pmBeginYmd;  // 판매개시일자
	String pmStopYmd;  // 판매중지일자
	String inskndCd;  // 보종코드
	String rquSelYn;  // 필수선택여부
	int pageFld; // 목차PDF하단페이징높이
	String prdtText;  // 상품내용
	String rgstYmd;  // 등록일자
	String mdfYmd;  // 수정일자
	String prdtChgYmd; //상품변경일자
	String delYn; // 삭제여부
}
