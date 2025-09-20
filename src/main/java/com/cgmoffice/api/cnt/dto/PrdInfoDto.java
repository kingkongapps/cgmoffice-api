package com.cgmoffice.api.cnt.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrdInfoDto {

	String cmpnyCode;  // 회사코드
	String cmpnyNm;  // 회사명
	String prdtCd;  // 상품코드
	String nprdtNm;  // 정식상품명
	String sprdtNm;  // 약식상품명
	String prdtCfcd;  // 상품구분코드

	String pmBeginYmd;  // 판매개시일자
	String pmStopYmd;  // 판매중지일자

	@Default
	String prdtChgYmd = "";  // 약관정보변경일자
	String prdtEndYmd;  // 약관정보종료일자

	String rgstYmd;  // 등록일자
	String mdfYmd;  // 수정일자
	String crtDtm;  // 생성일시
	String mdfDtm;  // 수정일시
	String crtr;  // 생성자
	String updusr;  // 수정자
	String prdtText;  // 상품내용
	String inskndCd;  // 보종코드
	String mnspccCfcd;  // 주특약구분코드
	String clusItmClcd;  // 약관항목분류코드
	String rquSelYn;  // 필수선택여부
	String delYn; // 삭제여부

	String dbDriverId; // db ID

	@Default
	float pageFld = 35;  // 목차PDF하단페이징높이

	@Default
	String fileNo = "";  // 파일ID

	@Default
	String fileNm = "";  // 파일명

	@Default
	String newYn = "Y";  // 신규여부

	@Default
	String addChangLogYn = "N";  // 변경이력 추가여부

	// 약관변경이력목록
	@Default
	List<PrdInfoChgLstDto> prdInfoChgLst = new ArrayList<PrdInfoChgLstDto>();

	@Builder
	@Getter @Setter
	@NoArgsConstructor
	@AllArgsConstructor
	static public class PrdInfoChgLstDto{

		String cmpnyCode;  // 회사코드
		String cmpnyNm;  // 회사명
		String prdtCd;  // 상품코드
		String nprdtNm;  // 정식상품명
		String sprdtNm;  // 약식상품명
		String prdtCfcd;  // 상품구분코드

		String pmBeginYmd;  // 판매개시일자
		String pmStopYmd;  // 판매중지일자

		String prdtChgYmd;  // 약관정보변경일자
		String prdtEndYmd;  // 약관정보종료일자

		String rgstYmd;  // 등록일자
		String mdfYmd;  // 수정일자
		String crtDtm;  // 생성일시
		String mdfDtm;  // 수정일시
		String crtr;  // 생성자
		String updusr;  // 수정자
		String prdtText;  // 상품내용
		String inskndCd;  // 보종코드
		String mnspccCfcd;  // 주특약구분코드
		String clusItmClcd;  // 약관항목분류코드
		String rquSelYn;  // 필수선택여부

		String dbDriverId; // db ID

		@Default
		String fileNo = "";  // 파일ID

		@Default
		String fileNm = "";  // 파일명
	}
}
