package com.cgmoffice.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDto {

	// 파일ID
	String fileNo;
	
	// 파일그룹No
	String fileGrpNo;

	// 파일명
	String fileNm;

	// 타입코드
	@Default
	String fileGb = "-";

	// 참조번호
	@Default
	String refNo = "";

	// 정렬순서
	@Default
	long sortNo = -1;

	// 파일용량
	long fileSize;

	// 파일확장자
	String fileExt;

	// 파일타입
	@Default
	String fileType = "";

	// 저장파일물리경로
	@Default
	String filePath = "";

	// 삭제여부
	@Default
	String delYn = "N";
	
	// 생성일시
	@Default
	String crtDtm = "";

	// 생성일시
	@Default
	String crtDtmChar = "";
	
	// 수정일시
	@Default
	String mdfDtm = "";
	
	// 생성자
	@Default
	String crtr = "";
	
	// 수정자
	@Default
	String amdr = "";
	
	// 기타
	@Default
	String rm = "";

}
