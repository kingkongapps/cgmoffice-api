package com.cgmoffice.api.sys.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.sys.dto.SearchSysMsgDto;
import com.cgmoffice.api.sys.dto.SysMsgDto;
import com.cgmoffice.api.sys.dto.SysMsgListDto;
import com.cgmoffice.api.sys.service.SysMsgMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/SysMsgMng")
@RequiredArgsConstructor
@Slf4j
public class SysMsgMngController {

	private final SysMsgMngService sysMsgMngService;

	//메시지코드 목록 조회
	@PostMapping("getListPage")
	public SysMsgListDto getListPage(@RequestBody SearchSysMsgDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<SysMsgDto> pageList = sysMsgMngService.getListPage(dto, pageConfig);

		return SysMsgListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

	//메시지코드 선택 삭제
	@PostMapping("deleteList")
	public void deleteList(@RequestBody List<SysMsgDto>  dtoList) {

		String memId = RequestUtils.getUser().getMemId();
		dtoList.forEach(dto -> dto.setMemId(memId));

		sysMsgMngService.deleteList(dtoList);
	}

	//메시지코드 신규등록
	@PostMapping("insertList")
	public void insertList(@RequestBody SysMsgDto  dtoList) {

		//생성자 아이디 셋팅
		String memId = RequestUtils.getUser().getMemId();
		dtoList.setMemId(memId);

		sysMsgMngService.insertList(dtoList);
	}

	//메시지코드 수정
	@PostMapping("updateList")
	public void updateList(@RequestBody SysMsgDto  dtoList) {

		//생성자 아이디 셋팅
		String memId = RequestUtils.getUser().getMemId();
		dtoList.setMemId(memId);

		sysMsgMngService.updateList(dtoList);
	}

	//메시지코드 아이디 중복체크
	@PostMapping("selectMsgIdYn")
	public String selectMsgIdYn(@RequestBody SysMsgDto dto) {

		String msgIdYn = sysMsgMngService.selectMsgIdYn( dto);

		return msgIdYn;
	}






}
