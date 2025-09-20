package com.cgmoffice.api.cnt.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.cnt.dto.PrdtClusDto;
import com.cgmoffice.api.cnt.service.PrdtClusMngService;
import com.cgmoffice.core.scheduler.ScheduledTasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cnt/prdtClusMng")
@RequiredArgsConstructor
@Slf4j
public class PrdtClusMngController {

	private final PrdtClusMngService prdtClusMngService;
	private final ScheduledTasks scheduledTasks;

	@GetMapping("getInfo")
	public List<PrdtClusDto> getInfo(@RequestParam String prdtCd) {
		return prdtClusMngService.getInfo(prdtCd);
	}

	@PostMapping("save")
	public void save(@RequestBody List<PrdtClusDto> dtolist) {
		prdtClusMngService.save(dtolist);
	}

	@GetMapping("batch")
	public void batch() {
		scheduledTasks.cron01();
	}

}
