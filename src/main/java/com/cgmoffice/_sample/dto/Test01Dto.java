package com.cgmoffice._sample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Test01Dto {

	int idx;

	String userName;

	int userAge;
}
