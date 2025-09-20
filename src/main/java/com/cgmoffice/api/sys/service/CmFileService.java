package com.cgmoffice.api.sys.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;
import com.cgmoffice.api.sys.dto.CmFileHistDto;
import com.cgmoffice.api.sys.dto.SearchCmFileHistDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.ExcelUtils;
import com.cgmoffice.core.utils.JsonUtils;
import com.cgmoffice.core.utils.UuidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CmFileService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;
	private final FileService commonFileService;
	private final String[] sheetHeaderNames = new String [] {"수정일시", "약관파일명", "약관파일ID", "생성자", "생성일시", "타입코드", "정렬순서", "파일용량", "파일확장자", "파일타입", "저장파일물리경로", "비고", "삭제여부"};

	//목록 조회 (페이징)
	public PageList<CmFileHistDto> selectFileListPage(SearchCmFileHistDto dto, PageConfig pageConfig){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		PageList<CmFileHistDto> list = appDao.selectListPage("api.sys.cmFile.selectFileListPage_TB_CM_FILE", dto, pageConfig);
		return list;
	}

	//목록 조회 (전체)
	public List<CmFileHistDto> selectFileList(SearchCmFileHistDto dto){
		dto.setDatabaseId(cmmnProperties.getAppDatabaseId());

		List<CmFileHistDto> list = appDao.selectList("api.sys.cmFile.selectFileList_TB_CM_FILE", dto);
		return list;
	}

	@SuppressWarnings("unchecked")
	public byte[] downloadExcel_01(List<CmFileHistDto> list) throws IOException {
		try (Workbook workbook = new XSSFWorkbook()) {
			ObjectMapper objectMapper = new ObjectMapper();
			Sheet sheet = workbook.createSheet("약관파일조회(이관목록)");
			int mapCnt = 0;

			// 헤더
			Row header = sheet.createRow(0);
			for(int i = 0; i < sheetHeaderNames.length; i++) {
				header.createCell(i).setCellValue(sheetHeaderNames[i]);
			}

			// 데이터
			for(int i = 0; i < list.size(); i++) {
				int rowNum = i + 1;
				Row row = sheet.createRow(rowNum);

				Map<String, Object> cvtMap = objectMapper.convertValue(list.get(i), Map.class);

				mapCnt = 0;

				for(Entry<String, Object> entry : cvtMap.entrySet()) {
					row.createCell(mapCnt).setCellValue(entry.getValue() != null ? entry.getValue().toString() : "");
					mapCnt++;
				}
			}

			// 열 너비 자동 조절 (모든 열에 대해 반복)
			for(int i = 0; i < list.size(); i++) {
				sheet.setColumnWidth(i, 20 * 256);
			}

			// OutputStream에 작성
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				workbook.write(out);
				return out.toByteArray();
			}
		}
	}

	public void excelUpLoad_01(MultipartFile excelFile) {
		List<List<String>> excelDataList = ExcelUtils.readExcel(excelFile);

		log.debug(">>> excelData: {}", JsonUtils.toJsonStr(excelDataList));

		String uploadRootPath = cmmnProperties.getFileMng().getUploadRootPath();
		String pdfDir = cmmnProperties.getPdfDir();

		int size = excelDataList.size();

		// 엑셀의 2번째 행부터 추출을 시작한다.
		for(int i=1; i < size; i++) {
			List<String> excelData = excelDataList.get(i);

			String mdfDtm = excelData.get(0); // 수정일시
			String fileNm = excelData.get(1); // 약관파일명
			String fileNo = excelData.get(2); // 약관파일ID
			String crtr = excelData.get(3); // 생성자
			String crtDtm = excelData.get(4); // 생성일시
			String fileGb = excelData.get(5); // 타입코드
			String sortNo = excelData.get(6); // 정렬순서
			String fileSize = excelData.get(7); // 파일용량
			String fileExt = excelData.get(8); // 파일확장자
			String fileType = excelData.get(9); // 파일타입
			String filePath = excelData.get(10); // 저장파일물리경로
			String rm = excelData.get(11); // 비고
			String delYn = excelData.get(12); // 삭제여부

			// 만약 filePath 경로가 없다면 기본 path로 대체
			if(StringUtils.isEmpty(StringUtils.trimToEmpty(filePath))) {
				filePath = uploadRootPath + pdfDir;
			}
			if(StringUtils.isEmpty(fileNo)) {
				throw new CmmnBizException("약관파일ID가 비어있습니다.");
			}
			if(StringUtils.isEmpty(fileNm)) {
				throw new CmmnBizException("약관파일명이 비어있습니다.");
			}
			if(StringUtils.isEmpty(fileGb)) {
				throw new CmmnBizException("타입코드가 비어있습니다.");
			}
			if(StringUtils.isEmpty(crtDtm)) {
				throw new CmmnBizException("생성일시가 비어있습니다.");
			}
			if(StringUtils.isEmpty(crtr)) {
				throw new CmmnBizException("생성자가 비어있습니다.");
			}

			// 파일ID로 파일목록을 추출하여 저장된 파일이 존재하는지를 체크한다.
			List<FileInfoDto> fileList = commonFileService.getFileInfoListByFileNo(fileNo);
			String isExistFile = fileList.size() == 0 ? "N" : "Y";
			String fileGrpNo = "-";

			FileInfoDto fileInfoDto = FileInfoDto.builder()
					.fileGrpNo(fileGrpNo)
					.fileNo(fileNo)
					.fileNm(fileNm)
					.fileGb(fileGb)
					.refNo("")
					.sortNo(Long.parseLong(sortNo))
					.fileSize(Long.parseLong(fileSize))
					.fileExt(fileExt)
					.fileType(fileType)
					.filePath(filePath)
					.rm(rm)
					.delYn(delYn)
					.crtDtm(crtDtm)
					.mdfDtm(mdfDtm)
					.crtr(crtr)
					.amdr(crtr)
					.build();

			//파일이 없는 경우에는 insert, 이미 파일이 있는 경우에는 update
			if("N".equals(isExistFile)) {
				commonFileService.insertFileInfo(fileInfoDto);
			} else {
				commonFileService.updateFileInfo(fileInfoDto);
			}
		}
	}

	//약관파일 목록 파일다운로드
	public ResponseEntity<Resource> fileDownLoad_01(SearchCmFileHistDto dto) {
		List<CmFileHistDto> list = this.selectFileList(dto);

		Resource resource = null;
		//출력 헤더셋팅
		HttpHeaders headers = null;

		// 압축 결과 파일
		String zipFileName = "약관파일조회(이관목록).zip";

		// 압축처리할 파일경로
		String zipFilePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
				.append(zipFileName)
				.toString();

		// 압축처리할 파일
		File zipFile = new File(zipFilePath);

		// 압축처리할 파일
		try (
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos)
		) {
			zos.setLevel(Deflater.BEST_COMPRESSION);

			// 중복된 이름 관리용 Set
			Set<String> existingNames = new HashSet<>();

			for (int i = 0; i < list.size(); i++) {
				String fileNo = list.get(i).getFileNo();
				String filePath = list.get(i).getFilePath();

				// 만약 filePath 경로가 없다면 기본 path로 대체
				if(StringUtils.isEmpty(StringUtils.trimToEmpty(filePath))) {
					filePath = cmmnProperties.getFileMng().getUploadRootPath();
				}

				File file = new File(filePath + File.separator + fileNo);
				if (!file.exists()) {
					continue;
				}

				// 파일명 중복 방지
				String uniqueName = file.getName();
				int counter = 1;
				while (existingNames.contains(uniqueName)) {
					int dotIndex = file.getName().lastIndexOf('.');
					String nameWithoutExt = (dotIndex == -1) ? file.getName() : file.getName().substring(0, dotIndex);
					String ext = (dotIndex == -1) ? "" : file.getName().substring(dotIndex);
					uniqueName = nameWithoutExt + "_" + counter + ext;
					counter++;
				}
				existingNames.add(uniqueName);

				try (FileInputStream fis = new FileInputStream(file)) {
					byte[] buffer = new byte[4096];

					zos.putNextEntry(new ZipEntry(uniqueName));

					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}

					zos.closeEntry();
				}
			}

			log.info("압축 완료: " + zipFileName);

			resource = new FileSystemResource(zipFile);

			// 한글명 인코딩
			String encodedFileName = URLEncoder
								 		.encode(zipFileName, StandardCharsets.UTF_8.toString())
								 		.replaceAll("\\+", "%20"); // 공백 처리

			// 출력 헤더셋팅
			headers = new HttpHeaders();
			headers.setContentType(MediaType.valueOf("application/zip"));
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", encodedFileName);
		} catch (IOException e) {
			throw new CmmnBizException("약관파일 목록 파일 다운로드 작업 중 오류가 발생했습니다.", e);
		}

		// 출력
		return ResponseEntity.ok()
				.headers(headers)
				.body(resource);
	}

	//약관파일 목록 파일업로드
	public void fileUpLoad_01(MultipartFile zipFile) {
		// 2시간이전의 임시폴더는 모두 삭제한다.
		this.deleteOldTmpFolder();

		String filePath = cmmnProperties.getFileMng().getUploadRootPath();
		String pdfDir = cmmnProperties.getPdfDir();

		// 파일명 추출
		String orgFilenmPrefix = StringUtils.defaultString(zipFile.getOriginalFilename(), "ZIP_FILE.zip");
		orgFilenmPrefix = orgFilenmPrefix.substring(0, orgFilenmPrefix.length() - 4);

		// 임시파일 생성한다.
		File tmpTaskFile = null;

		try {
			tmpTaskFile = File.createTempFile("zipFile-", UuidUtils.getUuidOnlyString());

			// 첨부한 pdf 파일을 임시파일로 복사한다.
			zipFile.transferTo(tmpTaskFile);

			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpTaskFile))) {
				ZipEntry entry;
				byte[] buffer = new byte[4096];

				while ((entry = zis.getNextEntry()) != null) {
					// 파일 생성
					File newFile = new File(filePath + pdfDir, entry.getName());

					if (entry.isDirectory()) {
						// 디렉토리 엔트리라면 폴더 생성
						newFile.mkdirs();
					} else {
						// 부모 폴더 생성
						new File(newFile.getParent()).mkdirs();

						// 파일 저장
						try (FileOutputStream fos = new FileOutputStream(newFile)) {
							int len;
							while ((len = zis.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
						}
					}
					zis.closeEntry();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			throw new CmmnBizException("약관파일 목록 파일업로드 중 오류가 발생", e);
		}
	}

	/**
	 * 2시간이전의 임시작업폴더는 모두 삭제
	 */
	private void deleteOldTmpFolder() {
		File rootDir = new File(cmmnProperties.getFileMng().getUploadRootPath());

		// temp_ 두시간 이전의 pdf 임시 작업폴더는 모두 삭제한다.
		String oldTimeHour = LocalDateTime.now()
				.minusHours(2)  // 두시간이전
				.format(DateTimeFormatter.ofPattern("yyyyMMddHH"))  // 포맷
				;
		File[] files = rootDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String folderName = file.getName();
				if (folderName.startsWith("temp_") && folderName.length() >= 13) {
					String timePart = folderName.substring(5, 15); // "2025042215xxx..." → "2025042215"
					if (timePart.compareTo(oldTimeHour) < 0) {
						CoreUtils.deleteDirectoryAndFiles(file); // 폴더 삭제
					}
				}
			}
		}
	}

}
