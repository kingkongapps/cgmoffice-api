package com.cgmoffice.core.constant;

public enum BaseResponseCode {
	/** Success */
	SE00000("00000", "Success"),
	/** 성공 */
	SK00000("00000", "성공"),
	
	/* ===================================
	 *   응답코드 멤버 변수 - 7자리 의미
	 * ------------ ----------------------
	 * 영문(첫번째): S[성공], F[실패]
	 * 영문(두번째): E[영문], K[한글] ...
	 * -----------------------------------
	 * ** client 응답 코드는 숫자 5자리 **
	 * ------------ ----------------------
	 * 숫자(첫번째): 0[성공], 1~9[실패]
	 * >나머지 숫자: 응답 코드 정의 *****
	 * -----------------------------------
	 * */
	
	/** Failure */
	FE10000("10000", "Failure"),
	/** 실패 */
	FK10000("10000", "실패"),
	
	
	/** Message not found in 'exception*.properties' */
	FE10001("10001", "Message not found in 'exception*.properties'"),
	/** exception*.properties' 파일에 등록된 message가 없습니다 */
	FK10001("10001", "'exception*.properties' 파일에 등록된 message가 없습니다"),
	
	/** Validation Error - check the Controller Response Type */
	FE10002("10002", "Validation Error - check the Controller Response Type"),
	/** Controller 입력의 파라미터의 Validation 오류 */
	FK10002("10002", "Controller 입력의 파라미터의 Validation 오류"),
	
	/** Validation Error for Reqeust parameter */
	FE40000("40000", "Validation Error for Reqeust parameter"),
	/** 요청 파라미터 오류 */
	FK40000("40000", "요청 파라미터 오류"),

	/** Error - convert Object to JSON String */
	FE40100("40100", "Error - convert Object to JSON String"),
	/** JSON 변환 오류 */
	FK40100("40100", "JSON 변환 오류"),
	/** Error - convert JSON String to Object */
	FE40101("40101", "Error - convert JSON String to Object"),
	/** 객체(from JSON String) 변환 오류 */
	FK40101("40101", "객체(from JSON String) 변환 오류"),
	
	/** Unauthorized */
	FE40401("40401", "Unauthorized"),
	/** 사용자 인증 실패 */
	FK40401("40401", "사용자 인증 실패"),
	/** Forbidden */
	FE40403("40403", "Forbidden"),
	/** 사용자 권한 오류 */
	FK40403("40403", "사용자 권한 오류"),
	
	/** Not Fount URL */
	FE40404("40404", "Not Found URL"),
	/** 존재하지 않는 URL */
	FK40404("40404", "존재하지 않는 URL"),
	
	/** Unknown Error of Server */
	FE50001("50001", "Unknown Error of Server"),
	/** 알수 없는 오류 */
	FK50001("50001", "알수 없는 오류"),
	
	/** Error to extract Object From CommonBaseDto */
	FE50002("50002", "Error to extract Object From CommonBaseDto"),
	/** 객체 추출 오류 - CommonBaseDto */
	FK50002("50002", "객체 추출 오류 - CommonBaseDto"),
	
	/** SQL Execution Error - Query Statement or Input Aurgument */
	FE50100("50100", "SQL Execution Error - Query Statement or Input Aurgument"),
	/** SQL 실행 오류 */
	FK50100("50100", "SQL 실행 오류"),
	;

	
	private String responseCode;
	private String responseMsg;
	
	BaseResponseCode(String rspCode, String rspMsg) {
		this.responseCode = rspCode;
		this.responseMsg  = rspMsg;
	}

	public String code() {
		return this.responseCode;
	}

	public String msg() {
		return this.responseMsg;
	}

}

