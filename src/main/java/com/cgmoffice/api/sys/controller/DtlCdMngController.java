package com.cgmoffice.api.sys.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.cnt.service.PrdtClusPdfMngService;
import com.cgmoffice.api.sys.dto.DtlCdDto;
import com.cgmoffice.api.sys.dto.DtlCdListDto;
import com.cgmoffice.api.sys.dto.SearchDtlCdDto;
import com.cgmoffice.api.sys.service.DtlCdMngService;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sys/DtlCdMng")
@RequiredArgsConstructor
@Slf4j
public class DtlCdMngController {

	private final DtlCdMngService dtlCdMngService;
	private final PrdtClusPdfMngService prdtClusPdfMngService;

	//목록 조회
	@PostMapping("getListPage")
	public DtlCdListDto getListPage(@RequestBody SearchDtlCdDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<DtlCdDto> pageList = dtlCdMngService.getListPage(dto, pageConfig);

		return DtlCdListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

	//목록 조회(페이징 미적용)
	@PostMapping("getList")
	public List<DtlCdDto> getList(@RequestBody SearchDtlCdDto dto) {

		List<DtlCdDto> list = dtlCdMngService.getList(dto);

		for(int i = 0; i < list.size(); i++) {
			if("".equals(list.get(i).getCdVal1())) {
				list.get(i).setQrImg("");
			} else {
				list.get(i).setQrImg("data:image/png;base64," + prdtClusPdfMngService.createQrCode(list.get(i).getCdVal1(), "Y"));
			}
		}

		return list;

	}

	//선택 삭제
	@PostMapping("deleteList")
	public void deleteList(@RequestBody List<DtlCdDto>  dtoList) {

		String user = RequestUtils.getUser().getMemId();
		dtoList.forEach(dto -> dto.setUser(user));

		dtlCdMngService.deleteList(dtoList);
	}

	//신규등록
	@PostMapping("insertList")
	public void insertList(@RequestBody DtlCdDto  dtoList) {

		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		dtlCdMngService.insertList(dtoList);
	}


	//수정
	@PostMapping("updateList")
	public void updateList(@RequestBody DtlCdDto  dtoList) {

		String user = RequestUtils.getUser().getMemId();
		dtoList.setUser(user);

		dtlCdMngService.updateList(dtoList);
	}

	//중복확인
	@PostMapping("selectCdYn")
	public String selectCdYn(@RequestBody DtlCdDto  dtoList) {

		String cdYn = dtlCdMngService.selectCdYn( dtoList);

		return cdYn;
	}






}
