package com.cgmoffice.api.cnt.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class PdfAutoSplitDto {

	@JsonIgnore
	MultipartFile inputPdf;

	String taskDir;

	List<String> fileNmList;

	List<String> pageList;

	String saveFileNm;

	// 서버에 저장된 파일명
	String fileNo;

	String strResList;

}
