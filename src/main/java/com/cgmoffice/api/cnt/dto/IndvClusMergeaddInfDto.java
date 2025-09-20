package com.cgmoffice.api.cnt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndvClusMergeaddInfDto {

	String indvClusMergeaddId;
	String indvClusMxtrId;
	String clusNm;
	String fileAtchDir;
	String emalSndYn;
	String emalAdr;
	String crtr;
	String updusr;
	String txtText;

}
