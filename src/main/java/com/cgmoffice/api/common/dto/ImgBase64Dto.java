package com.cgmoffice.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImgBase64Dto {

	// 이미지 base64 데이터
	String base64Data;

	// 파일확장자
	String fileExt;
}
