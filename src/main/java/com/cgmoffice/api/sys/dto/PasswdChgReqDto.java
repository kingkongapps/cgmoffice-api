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
public class PasswdChgReqDto {

	String userId;	//사용자아이디
	String pwCur;	//현재비밀번호
	String pwNew;	//변경비밀번호
	String pwCof;	//비밀번호확인
	String encodedPw;
}
