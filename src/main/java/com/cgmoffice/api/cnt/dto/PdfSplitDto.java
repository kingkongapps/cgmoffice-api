package com.cgmoffice.api.cnt.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

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
public class PdfSplitDto {

	@NotNull(message = "PDF 파일을 업로드해 주세요.")
	@JsonIgnore
    MultipartFile inputPdf;
	
	// 서버측 작업폴더
	String taskDir;
	
	// 쪼개진 pdf 파일들 미리보기정보 목록
	List<PdfPreviewDto> previewList;
}
