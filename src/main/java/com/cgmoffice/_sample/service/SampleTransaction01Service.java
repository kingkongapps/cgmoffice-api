package com.cgmoffice._sample.service;

import org.springframework.stereotype.Service;

import com.cgmoffice._sample.dto.Test01Dto;
import com.cgmoffice.core.dao.AppDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SampleTransaction01Service {

	private final AppDao appDao;
	private final SampleTransaction02Service sampleTransaction02Service;

	public Test01Dto getInfo() {
		return appDao.selectOne("sample.sampleTracsaction.getInfo");
	}

	public void reset() {
		appDao.update("sample.sampleTracsaction.reset");
	}

	public void update(int age) {
		appDao.update("sample.sampleTracsaction.test01", age);
	}

	public void test01(int age) {
		update(age);
	}

	public void test02(int age) {
		update(age);
		throw new RuntimeException();
	}

	public void test03(int age) {
		update(age);
		sampleTransaction02Service.update(age + 1);
	}

	public void test04(int age) {
		update(age);
		sampleTransaction02Service.test04(age + 1);
	}

	public void test05(int age) {
		update(age);
		sampleTransaction02Service.update(age + 1);
		throw new RuntimeException();
	}

}
