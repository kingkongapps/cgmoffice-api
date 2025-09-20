package com.cgmoffice.api.common.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelDownConfigDto {

	@NotBlank
	String fileName;

	@NotBlank
	String sheetName;

	@NotBlank
	List<ColumnInfo> colInfoList;

	@Getter @Setter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ColumnInfo{

		@NotBlank
		String dataColNm;

		@NotBlank
		String exelColNm;

		@NotBlank
		int width;
	}

}
