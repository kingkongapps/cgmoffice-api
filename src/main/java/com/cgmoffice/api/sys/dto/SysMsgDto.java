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
public class SysMsgDto {

	String msgId; //메시지ID
	String msgNm; //메시지명
	String msgType; //메시지유형
	String cdNm; //상세코드명
	String msgDesc; //메시지상세
	String crtDtm; //생성일시
	String memId; //생성자

	String databaseId; //디비종류
}
