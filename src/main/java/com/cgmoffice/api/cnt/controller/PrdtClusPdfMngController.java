package com.cgmoffice.api.cnt.controller;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.cnt.dto.IndvClusRcvMstDto;
import com.cgmoffice.api.cnt.dto.PdfAutoSplitDto;
import com.cgmoffice.api.cnt.dto.PdfInfoDto;
import com.cgmoffice.api.cnt.dto.PdfMergeDto;
import com.cgmoffice.api.cnt.dto.PdfSplitDto;
import com.cgmoffice.api.cnt.dto.PrdtClusPdfListDto;
import com.cgmoffice.api.cnt.dto.SearchPrdClusPdfDto;
import com.cgmoffice.api.cnt.dto.TocEntryDto;
import com.cgmoffice.api.cnt.service.PrdtClusPdfMngService;
import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cnt/prdtClusPdfMng")
@RequiredArgsConstructor
@Slf4j
public class PrdtClusPdfMngController {

	private final PrdtClusPdfMngService prdtClusPdfMngService;

	/**
	 * 약관조합코드에 의한 개별약관 생성
	 * @param dto
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@CrossOrigin(origins = "*") // 모든 도메인 허용
	@GetMapping("indvClusRcv")
	public ResponseEntity<Resource> indvClusRcv(@RequestParam String mxtrClusCd, @RequestParam(required = false, defaultValue = "") String sndurl, @RequestParam(required = false, defaultValue = "Y") String isMergeAddHistYn) throws UnsupportedEncodingException {
		return prdtClusPdfMngService.indvClusRcv(mxtrClusCd, sndurl, isMergeAddHistYn);
	}

	/**
	 * 약관조합코드에 의한 개별약관 특약별 분리다운로드
	 * @param dto
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@CrossOrigin(origins = "*") // 모든 도메인 허용
	@GetMapping("indvClusRcvSplitDn")
	public ResponseEntity<Resource> indvClusRcvSplitDn(@RequestParam String mxtrClusCd) throws UnsupportedEncodingException {

		return prdtClusPdfMngService.indvClusRcvSplitDn(mxtrClusCd);
	}

	/**
	 * PDF 약관분리작업
	 * @param dto
	 * @return
	 */
	@PostMapping("split")
	public PdfSplitDto split(@Valid @ModelAttribute PdfSplitDto dto) {
		return prdtClusPdfMngService.split(dto);
	}

	/**
	 * PDF 합본 서버저장 작업
	 * @param dto
	 * @return
	 */
	@PostMapping("mergeServer")
	public PdfMergeDto mergeServer(@Valid @RequestBody PdfMergeDto dto) {
		return prdtClusPdfMngService.mergeServer(dto);
	}

	/**
	 * 목차 리스트 추출
	 * @param dto
	 * @return
	 */
	@PostMapping("getTocList")
	public List<TocEntryDto> getTocList(@Valid @ModelAttribute PdfMergeDto dto) {
		return prdtClusPdfMngService.getTocList(dto);
	}

	/**
	 * PDF 자동분할 작업
	 * @param dto
	 * @return ResponseEntity<Resource>
	 */
	@PostMapping("autoSplitPdf")
	public ResponseEntity<Resource> autoSplitPdf(@Valid @ModelAttribute PdfAutoSplitDto dto) {
		return prdtClusPdfMngService.autoSplitPdf(dto);
	}

	/**
	 * PDF 합본 사용자 로컬PC 다운로드 작업
	 * @param dto
	 * @return
	 */
	@PostMapping("mergePC")
	public ResponseEntity<Resource> mergePC(@Valid @RequestBody PdfMergeDto dto) {
		return prdtClusPdfMngService.mergePC(dto);
	}

	/**
	 * 신규생성 작업
	 * @param pdfBlob
	 * @param saveFileNm
	 */
	@PostMapping("addNew")
	public void addNew(
			@RequestParam MultipartFile pdfBlob,
			@RequestParam String saveFileNm
			) {

		prdtClusPdfMngService.addNew(pdfBlob, saveFileNm);
	}

	/**
	 * PDF 약관파일 업로드 작업
	 * @param pdfFiles
	 * @return
	 */
	@PostMapping("upPdf")
	public List<FileInfoDto> pdfUp(
			@RequestParam List<MultipartFile> pdfUpFiles
			) {
		return prdtClusPdfMngService.upPdf(pdfUpFiles);
	}

	/**
	 * PDF 약관파일 업로드 작업
	 * @param pdfFiles
	 * @return
	 */
	@PostMapping("splitZipDn")
	public ResponseEntity<Resource> splitZipDn(
			@RequestParam MultipartFile pdfSplitZipDnFile
			) {
		return prdtClusPdfMngService.splitZipDn(pdfSplitZipDnFile);
	}

	@GetMapping("previewTmp")
	public String previewTmp(
			@RequestParam String taskDir,
			@RequestParam String tmpFileNm
			) {
		return prdtClusPdfMngService.previewTmp(taskDir, tmpFileNm);
	}

	@GetMapping("preview")
	public String preview(
			@RequestParam String fileNo,
			@RequestParam int sort
			) {
		return prdtClusPdfMngService.preview(fileNo, sort);
	}

	@PostMapping("preview")
	public String preview(@RequestBody PdfInfoDto dto) {
		return prdtClusPdfMngService.preview(dto.getFileNo(), dto.getSort());
	}

	@PostMapping("getListPage")
	public PrdtClusPdfListDto getListPage(@RequestBody SearchPrdClusPdfDto dto) {

		PageConfig pageConfig = dto.getPageConfig();

		PageList<FileInfoDto> pageList = prdtClusPdfMngService.getListPage(dto, pageConfig);

		return PrdtClusPdfListDto.builder()
				.list(pageList)
				.paginator(pageList.getPaginator())
				.build();
	}

	@PostMapping("getList")
	public PrdtClusPdfListDto getList(@RequestBody SearchPrdClusPdfDto dto) {

		List<FileInfoDto> list = prdtClusPdfMngService.getList(dto);

		return PrdtClusPdfListDto.builder()
				.list(list)
				.build();
	}

	@GetMapping("getPdfInfo")
	public PdfInfoDto getPdfInfo(@RequestParam String fileNo) {
		return prdtClusPdfMngService.getPdfInfo(fileNo);
	}

	@PostMapping("getPdfInfo")
	public PdfInfoDto getPdfInfo(@RequestBody PdfInfoDto dto) {
		return prdtClusPdfMngService.getPdfInfo(dto.getFileNo());
	}

	@PostMapping("deleteListPrevChk")
	public List<Map<String, Object>> deleteListPrevChk(@RequestBody List<FileInfoDto> dtoList) {
		return prdtClusPdfMngService.deleteListPrevChk(dtoList);
	}

	@PostMapping("deleteList")
	public void deleteList(@RequestBody List<FileInfoDto> dtoList) {
		prdtClusPdfMngService.deleteList(dtoList);
	}

	@PostMapping("getQrCode")
	public String getQrCode(@RequestBody IndvClusRcvMstDto dto) {
		if("".equals(dto.getMxtrClusCd())) {
			return "";
		} else {
			return "data:image/png;base64," + prdtClusPdfMngService.createQrCode(dto.getMxtrClusCd(), "Y");
		}
	}

}
