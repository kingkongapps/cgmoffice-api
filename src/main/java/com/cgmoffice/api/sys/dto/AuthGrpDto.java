package com.cgmoffice.api.sys.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthGrpDto {

	String grpCd;	/* 그룹코드 */
	String cd;      /* 상세코드 */
	String cdNm;  	/* 상세코드명 */
	String cdDesc;  /* 코드상세 */
	String sortNo;  /* 정렬순서 */
	String cdVal1;  /* 기타1 */
	String cdVal2;  /* 기타2 */
	String cdVal3;  /* 기타3 */
	String levSeq;	/* 레벨번호 */
	String useYn;  	/* 사용여부 */
}
