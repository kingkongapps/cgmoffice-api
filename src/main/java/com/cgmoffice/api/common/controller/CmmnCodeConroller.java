package com.cgmoffice.api.common.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.CmmnCdDto;
import com.cgmoffice.api.common.service.CmmnCodeService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/common/code")
@RequiredArgsConstructor
@Slf4j
public class CmmnCodeConroller {

	private final CmmnCodeService cmmnCodeService;

	/**
	 * 공통코드조회 (모든코드조회)
	 * @param grpCd
	 * @return
	 */
	@GetMapping("cmmnCdAll")
	public List<CmmnCdDto> cmmnCdAll(@RequestParam(required = false) String grpCd) {
		return cmmnCodeService.cmmnCdAll(grpCd);
	}
	
	/**
	 * 공통코드조회 (사용가능한 코드만 조회)
	 * @param grpCd
	 * @return
	 */
	@GetMapping("cmmnCd")
	public List<CmmnCdDto> cmmnCd(@RequestParam(required = false) String grpCd) {
		return cmmnCodeService.cmmnCd(grpCd);
	}
	
	/**
	 * 공통코드조회 (페이징)
	 * @param grpCd
	 * @param pageConfig
	 * @return
	 */
	@GetMapping("cmmnCdAllPaging")
	public CmmnMap cmmnCdAllPaging(@RequestParam String grpCd, PageConfig pageConfig) {
		
		PageList<CmmnCdDto> pageList = cmmnCodeService.cmmnCdAllPaging(grpCd, pageConfig);
		
		return new CmmnMap()
    			.put("list", pageList)
    			.put("pagingInfo", pageList.getPaginator());
	}
	
	/**
	 * 공통코드가 존재하는지 여부 확인
	 * @param dto
	 */
	@PostMapping("chkCmmnCd")
	public int chkCmmnCd(@Valid @RequestBody CmmnCdDto dto) {
		return cmmnCodeService.chkCmmnCd(dto);
	}
	
	/**
	 * 공통코드 저장
	 * @param dto
	 */
	@PostMapping("saveCmmnCd")
	public void saveCmmnCd(@Valid @RequestBody CmmnCdDto dto) {
		cmmnCodeService.saveCmmnCd(dto);
	}
	
	/**
	 * 공통코드 삭제
	 * @param dto
	 */
	@PostMapping("deleteCmmnCd")
	public void deleteCmmnCd(@Valid @RequestBody CmmnCdDto dto) {
		cmmnCodeService.deleteCmmnCd(dto);
	}
	
	/**
	 * 공통코드목록 삭제
	 * @param dtoList
	 */
	@PostMapping("deleteCmmnCdList")
	public void deleteCmmnCdList(@Valid @RequestBody List<CmmnCdDto>  dtoList) {
		cmmnCodeService.deleteCmmnCdList(dtoList);
	}
}
