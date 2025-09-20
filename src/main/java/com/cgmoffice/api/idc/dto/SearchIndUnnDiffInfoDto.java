package com.cgmoffice.api.idc.dto;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndUnnDiffInfoDto {
	String prdtCd;  // 주계약상품코드
	String dbDriverId; //db ID

	PageConfig pageConfig;
}
