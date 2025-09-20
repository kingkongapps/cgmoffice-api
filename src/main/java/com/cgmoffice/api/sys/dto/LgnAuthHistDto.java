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
public class LgnAuthHistDto {

	String lgnSeq;  // 접속일련번호
	String memId;  // 맴버번호
	String memNm;  // 성명
	String acsDt;  // 접속일
	String acsIp;  // 접속IP
	String lgnScsYn;  // 로그이성공여부
	String loginCfcd;  // 로그인구분코드
	String delYn;  // 삭제여부
	String crtDtm;  // 삭제여부
}

