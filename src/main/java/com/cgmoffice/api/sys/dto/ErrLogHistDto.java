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
public class ErrLogHistDto {

	String errLogid; //에러로그아이디
	String occurDtm; //발생일시
	String methNm; //메소드명
	String sysDlngCfcd; //시스템처리구분코드
	String dlngTime; //처리시간
	String reqr; //요청자
	String crtDtm; //생성일시
	String errDtl;//에러상세

}



