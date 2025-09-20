package com.cgmoffice.api.sys.dto;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCmFileHistDto {

	@Default
	String cmpnyCode = ""; //회사코드

	@Default
	String startMdfDtm = ""; //시작수정일시(FROM)

	@Default
	String endMdfDtm = ""; //종료수정일시(TO)

	String databaseId; // db ID

	PageConfig pageConfig;

}
