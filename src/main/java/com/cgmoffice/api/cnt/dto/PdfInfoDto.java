package com.cgmoffice.api.cnt.dto;

import java.util.ArrayList;
import java.util.List;

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
public class PdfInfoDto {

	String fileNo;

	int sort;

	// 쪼개진 pdf 파일들 미리보기정보 목록
	@Default
	List<PdfPreviewDto> previewList = new ArrayList<PdfPreviewDto>();
}
