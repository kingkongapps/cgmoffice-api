package com.cgmoffice.api.sys.dto;

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
public class SysMsgListDto {

	List<SysMsgDto> list; //시스템메시지리스트

	Paginator paginator;

}
