package com.cgmoffice.api.sys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.service.DashboardMngService;
import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/dashboardMng")
@RequiredArgsConstructor
@Slf4j
public class DashboardMngController {

	private final DashboardMngService dashboardMngService;

	@GetMapping("getInfo")
	public CmmnMap getInfo(){
		return dashboardMngService.getInfo();
	}
}
