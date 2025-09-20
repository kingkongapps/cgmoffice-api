package com.cgmoffice._sample.service;

import org.springframework.stereotype.Service;

import com.cgmoffice.core.dao.AppDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SampleTransaction02Service {

	private final AppDao appDao;

	public void update(int age) {
		appDao.update("sample.sampleTracsaction.test01", age);
	}

	public void test04(int age) {
		update(age);
		throw new RuntimeException();
	}

}
