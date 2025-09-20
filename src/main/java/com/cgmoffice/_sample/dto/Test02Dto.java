package com.cgmoffice._sample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Test02Dto {

    @NotBlank
    @Size(min = 3, max = 50)
	String userName;

    @NotBlank
    String userNickname;

    String userId;

}
