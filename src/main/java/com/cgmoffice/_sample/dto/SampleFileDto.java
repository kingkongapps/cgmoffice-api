package com.cgmoffice._sample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SampleFileDto {

	String idx;
	String memo;
	String fileNm;
	String fileNo;
}
