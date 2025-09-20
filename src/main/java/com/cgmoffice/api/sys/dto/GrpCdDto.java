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
public class GrpCdDto {

	String grpCd; // 그룹코드
	String rgstYmd; // 등록일자
	String grpCdNm; // 그룹코드명
	String grpDesc; // 그룹코드상세
	String delYn; // 삭제여부
	String useYn; // 삭제여부
	String user; //사용자

	String databaseId; //디비종류

}
