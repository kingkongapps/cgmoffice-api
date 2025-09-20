package com.cgmoffice.api.cuc.dto;

import java.util.List;

import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.Paginator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClusResHisListDto {
	List<ClusResHisDto> list;

	Paginator paginator;
}

