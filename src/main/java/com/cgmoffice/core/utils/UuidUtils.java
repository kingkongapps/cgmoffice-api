package com.cgmoffice.core.utils;

import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UuidUtils {
	/**
	 * 32비트의 랜덤한 값을 생성
	 * @return
	 */
	public String getUuidOnlyString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
