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
public class DtlCdDto {

	String grpCd; // 그룹코드
	String grpCdNm; // 그룹코드명
	String cd; // 상세코드
	String cdNm; // 상세코드명
	String cdDesc; // 코드상세
	int sortNo; // 정렬순서
	String crtDtm; // 생성일시
	int levSeq; // 레벨번호
	String cdVal1; // 기타1
	String cdVal2; // 기타2
	String cdVal3; // 기타3
	String delYn; // 삭제여부
	String useYn; // 삭제여부
	String user; //

	String databaseId; //디비종류
	String qrImg; //qrImg Url
}


