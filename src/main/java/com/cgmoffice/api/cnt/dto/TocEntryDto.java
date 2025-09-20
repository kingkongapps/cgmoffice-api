package com.cgmoffice.api.cnt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TocEntryDto {

	public String number;
	public String title;
	public String selected;
	public String regex;
	public int startPage;
	public int endPage;
	public int firstPage;
	public int totalPages;
	public int idx;

}