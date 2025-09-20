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
public class SearchPrdDto {

	@Default
	String cmpnyCode = "";  // 회사코드

	@Default
	String prdtCd = ""; // 상품코드

	@Default
	String prdtNm = ""; // 상품명

	@Default
	String prdtChgYmd = ""; // 변경일자

	@Default
	String mnspccCfcd = ""; // 주특약구분코드

	@Default
	String clusItmClcd = ""; // 항목분류코드

	@Default
	String prdtCfcd = ""; // 상품구분코드

	@Default
	String rquSelYn = ""; // 필수선택여부(특약)

	String dbDriverId;

	PageConfig pageConfig;
}
