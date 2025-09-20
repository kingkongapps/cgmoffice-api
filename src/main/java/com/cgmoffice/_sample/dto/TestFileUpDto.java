package com.cgmoffice._sample.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestFileUpDto {

	@JsonIgnore
    private MultipartFile attachFile;

    private String memo;

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class testData {
    	String aaa;
    }
}
