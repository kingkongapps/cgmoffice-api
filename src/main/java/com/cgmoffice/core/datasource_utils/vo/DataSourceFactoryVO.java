package com.cgmoffice.core.datasource_utils.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceFactoryVO {

	String url;
	String username;
	String password;
	String driverClassName;
	
}
