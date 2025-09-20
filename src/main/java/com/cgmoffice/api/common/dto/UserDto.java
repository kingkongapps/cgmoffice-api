package com.cgmoffice.api.common.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	//@NotBlank(message = "사용자 아이디를 입력해 주세요.")
	//@Size(min = 3, max = 10, message = "사용자 아이디는 최소3자리, 최대 10자리 입니다.")
	String memId;

	// 보안상의 이유로 비밀번호는 JSON 응답에서 숨기고 싶지만, JSON 요청으로부터는 받아야 하는 경우.
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	//@NotBlank(message = "비밀번호를 입력해 주세요.")
	//@Size(min = 3, max = 100, message = "비밀번호는 최소3자리 이상, 최대 13자리 이하입니다.")
	String passwd;

	String memNm;

	@Default
	String email="";

	@Default
	String useYn="Y";

	@Default
	String authCd="";

	@Default
	String authGrpCd="";

	@Default
	String comCode = "";

	@Default
	String comName = "";

	@Default
	String findAcYn = "";

	@Default
	String lgnScsYn = "";

	@Default
	String viewId = "";

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Default
	String crtr="";

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Default
	String amdr="";

	List<MenuDto> menuList;

}
