package com.cgmoffice.api.cnt.dto;

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
public class SearchMnClusMppgDto {

	@Default
	String cmpnyCode = "";	//회사코드

	@Default
	String prdtCfcd = "";	//상품구분코드

	@Default
	String prdtCd = "";		//상품명

	@Default
	String databaseId = "";	//db ID

	PageConfig pageConfig;	//페이징정보

}
