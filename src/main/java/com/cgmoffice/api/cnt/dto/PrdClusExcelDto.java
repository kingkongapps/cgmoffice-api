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
public class PrdClusExcelDto {
	String num;  //순번
	String clusItmCd;  // 약관항목코드
	String clusItmNm;  // 약관항목명
	String clusItmClcd;  // 약관항목분류코드
	String prdtCd;  // 주계약상품코드
	String screnDispOrd;  // 목차순서
	String sn;  // 약관생성순서
	String delYn;  // 삭제여부
	String dataTransSttcd;  // 데이터전송상태코드
}
