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
public class AuthDto {

	String authCd;		/* 권한코드 */
	String menuCd;      /* 메뉴코드 */
	String allYn;  		/* 전체권한여부 */
	String selectYn;  	/* 조회권한여부 */
	String insertYn;   	/* 등록권한여부 */
	String updateYn;    /* 수정권한여부 */
	String deleteYn;    /* 삭제권한여부 */
	String excelYn;   	/* 엑셀조회권한여부 */
	String authNm;      /* 권한명 */
	String supAuthYn;  	/* 슈퍼권한여부 */
	String authGrpCd;   /* 권한그룹코드 */
	String menuNm;      /* 메뉴명 */
	String upprMenuCd;  /* 상위메뉴코드 */
	String upprMenuNm;  /* 상위메뉴명 */
	String authGrpNm;	/* 권한그룹명 */
	String newYn;		/* 신규여부 */
	String user;
}
