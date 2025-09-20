package com.cgmoffice.core.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.cgmoffice.core.utils.MessageI18nUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
public class ResCommonBaseDto<T> {

	private String httpStatus;

	private String responseCode;

	private String responseMessage;

	private T      responseData;


	public ResCommonBaseDto(T responseData) {
		this.httpStatus      = String.valueOf(HttpStatus.OK.value());
		this.responseCode    = MessageI18nUtils.getMessage("global.success.code");
		this.responseMessage = MessageI18nUtils.getMessage("global.success.message");
		this.responseData    = responseData;
	}

	@Builder
	public ResCommonBaseDto(String httpStatus, String responseCode, String responseMessage, T responseData) {
		this.httpStatus   = httpStatus;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.responseData    = responseData;
	}

}
