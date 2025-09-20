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
public class PdfPreviewDto {

	// 순서
	int sort;

	// 미리보기 이미지 base64
	String previewBase64;

	// 임시저장된 파일명
	String tmpFileNm;

	// 메인페이지 여부
	String isMainPage;

}
