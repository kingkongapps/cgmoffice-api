package com.cgmoffice.api.sys.dto;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchSysMsgDto {

	@Default
	String msgType = ""; //메시지유형 (E:업무오류, F:시스템에러, S:성공 I:정보, W:경고)

	@Default
	String msgNm = ""; //메시지명

	String databaseId; //디비종류

	PageConfig pageConfig;

}
