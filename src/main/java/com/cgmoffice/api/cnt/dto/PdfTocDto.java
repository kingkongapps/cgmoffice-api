package com.cgmoffice.api.cnt.dto;

import java.util.List;

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
public class PdfTocDto {

	// 제목
	String title;
	
	// 목차리스트
	List<TocContent> tocContentList;
	
	@Getter @Setter
	@Builder @ToString
	@AllArgsConstructor
	@NoArgsConstructor
	static public class TocContent{
		
		String title; // 제목
		String cls;  // 종류
		String page;  // 페이지
	}
}
