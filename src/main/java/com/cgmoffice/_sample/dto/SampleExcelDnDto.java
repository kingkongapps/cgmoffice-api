package com.cgmoffice._sample.dto;

import com.cgmoffice.api.common.dto.ExcelDownConfigDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SampleExcelDnDto {

	String userName;

	// 엑셀다운로드 설정정보
	ExcelDownConfigDto excelDownConfig;
}
