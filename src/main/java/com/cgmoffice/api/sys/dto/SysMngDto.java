package com.cgmoffice.api.sys.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysMngDto {

	List<MenuDto> menuList; 		//메뉴리스트
	List<AuthGrpDto> authGrpList; 	//권한그룹리스트
	List<AuthDto> authList; 		//권한리스트

	String returnMsg;
}
