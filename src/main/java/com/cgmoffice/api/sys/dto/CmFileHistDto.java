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
public class CmFileHistDto {

	String mdfDtm;		//수정일시
	String fileNm;		//약관파일명
	String fileNo;		//약관파일ID
	String crtr;		//생성자
	String crtDtm;		//생성일시
	String fileGb;		//타입코드
	String sortNo;		//정렬순서
	String fileSize;	//파일용량
	String fileExt;		//파일확장자
	String fileType;	//파일타입
	String filePath;	//저장파일물리경로
	String rm;			//비고
	String delYn;		//삭제여부

}