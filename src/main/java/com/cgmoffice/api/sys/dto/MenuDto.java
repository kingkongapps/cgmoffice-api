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
public class MenuDto {

	String menuCd;		/* 메뉴코드 */
	String menuNm;      /* 메뉴명 */
	String upprMenuCd;  /* 상위메뉴코드 */
	String menuTypCd;   /* 메뉴유형코드 */
	String menuCfcd;    /* 메뉴구분코드 */
	String menuLev;     /* 메뉴레벨 */
	String menuDtext;   /* 메뉴상세내용 */
	String sortNo;      /* 정렬순서 */
	String menuDispYn;  /* 메뉴표시여부 */
	String pgid;        /* 프로그램ID */
	String pgUrlAdr;    /* 프로그램URL주소 */
	String upprMenuNm;  /* 상위메뉴명 */
	String upprMenuLev;  /* 상위메뉴레벨 */
}
