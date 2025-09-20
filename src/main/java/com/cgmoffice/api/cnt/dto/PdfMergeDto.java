package com.cgmoffice.api.cnt.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

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
public class PdfMergeDto {

	@JsonIgnore
    MultipartFile inputPdf;

	@NotBlank(message = "분할된 pdf가 존재하는 서버측 임시작업폴더를 입력해주세요.")
	String taskDir;

	@NotEmpty(message = "합본할 PDF를 선택해주세요.")
	List<String> fileNmList;

	List<String> pageList;

	@NotBlank(message = "저장할 파일명을 입력해주세요.")
	String saveFileNm;

	// 서버에 저장된 파일명
	String fileNo;

}
