package com.cgmoffice._sample.service;

import org.springframework.stereotype.Service;

import com.cgmoffice._sample.util.SampleConstants;
import com.cgmoffice.core.utils.CmmnMap;

@Service
public class SampleThreadLocalService {

	public CmmnMap test01() {
		return new CmmnMap()
				.put("rslt", SampleConstants.temp.get())
				;
	}

	public CmmnMap test02() {
		return new CmmnMap()
				.put("rslt", SampleConstants.temp.get())
				;
	}
}
