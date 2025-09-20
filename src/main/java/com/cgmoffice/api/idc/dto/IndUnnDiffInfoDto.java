package com.cgmoffice.api.idc.dto;

import java.util.List;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.Paginator;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndUnnDiffInfoDto {

	String clusItmCd;  // 약관항목코드
	String clusItmNm;  // 약관항목명
	String clusItmClcd;  // 약관항목분류코드
	String prdtCd;  // 주계약상품코드
	Integer screnDispOrd;  // 목차순서
	Integer sn;  // 약관생성순서
	String crtr;  // 생성자
	String updusr;  // 수정자
	String cmpnyCode;  // 회사코드

	String mnspccCfcd; // 주특약구분코드
	String mncntrctSpccNm; // 주계약특약명
	String inskndCd;  // 보종코드
	String rquSelYn;  // 필수선택여부

	String dbDriverId; //db ID

	String isDiff;

	PageConfig pageConfig;

	List<IndUnnDiffInfoDto> list;

	Paginator paginator;
}
