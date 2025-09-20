package com.cgmoffice.api.sys.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchAdmUsrDto {

	@Default
	String memId = ""; //아이디

	@Default
	String memNm = ""; //이름

	@Default
	String comCode = ""; //회사코드

	String databaseId; //디비종류

}
