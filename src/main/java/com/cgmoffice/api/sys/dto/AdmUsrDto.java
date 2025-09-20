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
public class AdmUsrDto {

	String memId; // 멤버아이디
	String passwd; // 패스워드
	String memNm; // 성명
	String useYn; // 사용여부
	String email; // 이메일
	String loginCnt; // 로그인실패횟수
	String comCode; // 계열사코드
	String comCodeNm; // 계열사코드
	String authCd; // 권한코드
	String lastLgnDt; // 최종접속일
	String findAcYn; // 계정찾기요청여부
	String delYn; // 삭제여부
	String crtDtm; // 생성일시
	String cdNm; // 권한명
	String user;

	String databaseId; //디비종류

	String authNm;      //권한명
	String authGrpCd;   //권한그룹코드

}
