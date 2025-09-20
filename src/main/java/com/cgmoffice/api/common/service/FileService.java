package com.cgmoffice.api.common.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
	private final CmmnProperties cmmnProperties;
	private final AppDao appDao;
	private final ResourceLoader resourceLoader;



	public FileInfoDto uploadFile(
			String prefix,
			String fileGrpNo,
			MultipartFile multipartFile,
			String uploadRelativePath) {
		return uploadFile(prefix, fileGrpNo, multipartFile, uploadRelativePath, null, null);
	}

	/**
	 * 파일을 저장하는 함수
	 * @param multipartFile 파일저장 root 경로
	 * @param uploadRelativePath 파일저장 root 의 상대경로
	 * @return 저장한 파일정보
	 */
	public FileInfoDto uploadFile(
			String prefix,
			String fileGrpNo,
			MultipartFile multipartFile,
			String uploadRelativePath,
			String fileNm,
			String rm) {

		String filePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(uploadRelativePath)
				.toString(); // 파일저장경로
		File folderPath = new File(filePath);
		if (!folderPath.exists()) {
			folderPath.mkdirs();
		}

		prefix = StringUtils.isNotEmpty(prefix) ? StringUtils.join(prefix, '_') : "";
		if(StringUtils.isEmpty(fileGrpNo)) {
			fileGrpNo = prefix;
		}
		if(StringUtils.isEmpty(fileGrpNo)) {
			fileGrpNo = "-";
		}

		String fileType = CoreUtils.normalizeNFC(multipartFile.getContentType());


		if(StringUtils.isEmpty(fileNm)) {
			fileNm = multipartFile.getOriginalFilename();  // 원시파일명
		}

		fileNm = CoreUtils.normalizeNFC(fileNm);
		String fileExt = fileNm.substring(fileNm.lastIndexOf(".") + 1); // 파일확장자
		String fileNmPrevExt = fileNm.substring(0, fileNm.lastIndexOf("."));
		if (fileExt.length() < 3 || fileExt.length() > 4) {
			throw new CmmnBizException("업로드가 불가한 파일입니다.");
		}

		String[] fileExtArry = cmmnProperties.getFileMng().getUploadDenyExtList().split(",");
		for (String str : fileExtArry) {
			if (str.equalsIgnoreCase(fileExt)) {
				throw new CmmnBizException("확장자(" + fileExt + ")파일은 업로드가 불가합니다.");
			}
		}

		long uploadMaxSize;
		long fileSize = multipartFile.getSize(); // 파일크기
		if ("mp4".equalsIgnoreCase(fileExt)) {
			uploadMaxSize = cmmnProperties.getFileMng().getUploadVideoMaxSize(); // 동영상 파일업로드 최대사이즈
			if (fileSize > uploadMaxSize) {
				throw new CmmnBizException(
						new StringBuilder()
						.append("동영상 업로드 파일크기가 ")
						.append(uploadMaxSize/1000000)
						.append("MByte를 초과할 수 없습니다.")
						.toString()
					);
			}
		} else {
			uploadMaxSize = cmmnProperties.getFileMng().getUploadMaxSize(); // 파일업로드 최대사이즈
			if (fileSize > uploadMaxSize) {
				throw new CmmnBizException(
						new StringBuilder()
						.append("업로드 파일크기가 ")
						.append(uploadMaxSize/1000000)
						.append("MByte를 초과할 수 없습니다.")
						.toString()
					);
			}
		}

		// 저장파일명
		String fileNo = new StringBuilder()
				.append(prefix)
				.append(fileNmPrevExt)
				.append('_')
				.append(CoreUtils.genTimestampUniqId())
				.append('.')
				.append(fileExt)
				.toString();

		String fileFullPath = new StringBuilder()
				.append(filePath)
				.append(File.separator)
				.append(fileNo)
				.toString();
		File file = new File(fileFullPath);
		try {
			multipartFile.transferTo(file);
		} catch (IOException e) {
			throw new CmmnBizException("파일업로드를 실패했습니다.", e);
		}

		if(StringUtils.isEmpty(rm)) {
			rm = "";
		}

		FileInfoDto fileInfoDto = FileInfoDto.builder()
				.fileNo(fileNo)
				.fileNm(fileNm)
				.fileGrpNo(fileGrpNo)
				.fileGb("-")
				.fileExt(fileExt)
				.filePath(filePath)
				.fileSize(fileSize)
				.fileType(fileType)
				.delYn("N")
				.rm(rm)
				.crtr(RequestUtils.getUser().getMemId())
				.amdr(RequestUtils.getUser().getMemId())
				.build();

		insertFileInfo(fileInfoDto);

		return fileInfoDto;
	}

	/**
	 * 파일을 삭제한다.
	 * @param fileNo
	 */
	public void delete(String fileNo) {
		FileInfoDto fileInfoDto = getFileInfo(fileNo);
		String fileFullPath = new StringBuilder()
				.append(fileInfoDto.getFilePath())
				.append(File.separator)
				.append(fileNo)
				.toString();
		File file = new File(fileFullPath);
		if (!file.exists() || !file.isFile()) {
			log.error(">>> 파일이 존재하지 않습니다. : {}", fileFullPath);
		} else {
			file.delete();
		}

		fileInfoDto
			.setAmdr(RequestUtils.getUser().getMemId());

//		appDao.update("common.file.setFileDelete", fileInfoDto);

		appDao.delete("common.file.delete", fileInfoDto);
	}

	/**
	 * 파일메타정보를 관리하는 테이블에 해당정보를 저장
	 * @param fileInfoDto
	 */
	public void insertFileInfo(FileInfoDto fileInfoDto) {

		// mac 계열에서 올릴시 이슈해결
		// DB 에서 like 검색시 찾지를 못한다.
		fileInfoDto.setFileNo(CoreUtils.normalizeNFC(fileInfoDto.getFileNo()));
		fileInfoDto.setFileNm(CoreUtils.normalizeNFC(fileInfoDto.getFileNm()));
		fileInfoDto.setFileGrpNo(CoreUtils.normalizeNFC(fileInfoDto.getFileGrpNo()));
		fileInfoDto.setFileExt(CoreUtils.normalizeNFC(fileInfoDto.getFileExt()));
		fileInfoDto.setFilePath(CoreUtils.normalizeNFC(fileInfoDto.getFilePath()));
		fileInfoDto.setFileType(CoreUtils.normalizeNFC(fileInfoDto.getFileType()));
		fileInfoDto.setRm(CoreUtils.normalizeNFC(fileInfoDto.getRm()));
		fileInfoDto.setRefNo(CoreUtils.normalizeNFC(fileInfoDto.getRefNo()));


		appDao.insert("common.file.insertFileInfo", fileInfoDto);
	}

	/**
	 * 파일정보를 추출한다.
	 * @param fileNo
	 * @return
	 */
	public FileInfoDto getFileInfo(String fileNo) {
		return appDao.selectOne("common.file.getFileInfo", fileNo);

	}

	/**
	 * 파일ID로 파일목록을 추출한다.
	 * @param fileNo
	 * @return
	 */
	public List<FileInfoDto> getFileInfoListByFileNo(String fileNo) {
		return appDao.selectList("common.file.getFileInfoListByFileNo", CoreUtils.normalizeNFC(fileNo));
	}

	/**
	 * 파일명으로 파일목록을 추출한다.
	 * @param fileNm
	 * @return
	 */
	public List<FileInfoDto> getFileInfoListByFileNm(String fileNm) {
		return appDao.selectList("common.file.getFileInfoListByFileNm", CoreUtils.normalizeNFC(fileNm));
	}

	public ResponseEntity<Resource> download(String fileNo) throws UnsupportedEncodingException, FileNotFoundException {
		FileInfoDto fileInfoDto = getFileInfo(fileNo);
		if(fileInfoDto == null) {
			throw new FileNotFoundException();
		}
		String fileFullPath = new StringBuilder()
				.append(fileInfoDto.getFilePath())
				.append(File.separator)
				.append(fileNo)
				.toString();

		log.debug(">>> fileFullPath: {}", fileFullPath);
		fileFullPath = CoreUtils.normalizeNFC(fileFullPath);

		File file = new File(fileFullPath);
		if(!file.exists()) {
			throw new FileNotFoundException();
		}

		Resource resource = new FileSystemResource(file);

		// 한글명 인코딩
    	String encodedFileName = URLEncoder
					    	 		.encode(fileInfoDto.getFileNm(), StandardCharsets.UTF_8.toString())
					    	 		.replaceAll("\\+", "%20"); // 공백 처리

    	// 출력 헤더셋팅
        HttpHeaders headers = new HttpHeaders();
//      headers.setContentType(MediaType.parseMediaType(fileInfoDto.getFileType()));
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", encodedFileName);
        headers.remove(HttpHeaders.CONTENT_ENCODING); // 명시적으로 제거

        // 출력
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
	}

	public void deleteDir(String targetDir) {
		String targetTaskDir = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
        		.append(targetDir)
				.toString();

		CoreUtils.deleteDirectoryAndFiles(new File(targetTaskDir));
	}

	public void updateFileInfo(FileInfoDto dto) {

		// mac 계열에서 올릴시 이슈해결
		// DB 에서 like 검색시 찾지를 못한다.
		dto.setFileNo(CoreUtils.normalizeNFC(dto.getFileNo()));
		dto.setFileNm(CoreUtils.normalizeNFC(dto.getFileNm()));
		dto.setFileGrpNo(CoreUtils.normalizeNFC(dto.getFileGrpNo()));
		dto.setFileExt(CoreUtils.normalizeNFC(dto.getFileExt()));
		dto.setFilePath(CoreUtils.normalizeNFC(dto.getFilePath()));
		dto.setFileType(CoreUtils.normalizeNFC(dto.getFileType()));
		dto.setRm(CoreUtils.normalizeNFC(dto.getRm()));
		dto.setRefNo(CoreUtils.normalizeNFC(dto.getRefNo()));

		dto.setAmdr(RequestUtils.getUser().getMemId());

		appDao.update("common.file.updateFileInfo", dto);
	}

	public ResponseEntity<Resource> dnExcelTemplate(String code) throws IOException {

		Resource resource = null;
		String fileNm;
		if("template01".equals(code)) {
			resource = resourceLoader.getResource("classpath:static/storage/template01.xlsx");
			fileNm = "상품등록_템플릿.xlsx";
		} else if("template02".equals(code)) {
			resource = resourceLoader.getResource("classpath:static/storage/template02.xlsx");
			fileNm = "약관구성등록_템플릿.xlsx";
		} else if("template03".equals(code)) {
			resource = resourceLoader.getResource("classpath:static/storage/template01.xlsx");
			fileNm = "약관구성등록_템플릿2.xlsx";
		} else if("template04".equals(code)) {
			resource = resourceLoader.getResource("classpath:static/storage/template02.xlsx");
			fileNm = "약관구성등록_템플릿3.xlsx";
		} else {
			throw new CmmnBizException("다운로드 대상 엑셀템플릿 코드 오류발생!");
		}

		// 한글명 인코딩
    	String encodedFileName =
    			URLEncoder
    				.encode(fileNm, StandardCharsets.UTF_8.toString())
		    	 	.replaceAll("\\+", "%20"); // 공백 처리

    	// 출력 헤더셋팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", encodedFileName);
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        // 출력
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
	}
}
