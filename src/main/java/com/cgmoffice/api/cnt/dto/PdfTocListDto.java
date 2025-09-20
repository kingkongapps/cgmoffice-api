package com.cgmoffice.api.cnt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Builder @ToString
@AllArgsConstructor
@NoArgsConstructor
public class PdfTocListDto {

	// 목차명
	String tocName;

	// 페이지시작
	String startPage;

	// 페이지종료
	String endPage;

	// 페이지종료
	String idx;

}
