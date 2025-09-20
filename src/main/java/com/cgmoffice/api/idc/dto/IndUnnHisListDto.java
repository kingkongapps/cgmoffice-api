package com.cgmoffice.api.idc.dto;

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
public class IndUnnHisListDto {
	List<IndUnnHisDto> list;

	Paginator paginator;
}

