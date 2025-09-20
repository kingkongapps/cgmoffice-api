package com.cgmoffice.api.common.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.dto.ImgBase64Dto;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.RequestUtils;
import com.cgmoffice.core.utils.UuidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImgService {
	private final CmmnProperties cmmnProperties;
	private final FileService fileService;

	/**
	 * 이미지파일 올리기
	 * @param imageFile
	 * @param uploadRelativePath
	 * @return
	 */
	public FileInfoDto uploadImg(MultipartFile imageFile, String uploadRelativePath) {

		String filePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(uploadRelativePath)
				.toString(); // 파일저장경로
		File folderPath = new File(filePath);
		if (!folderPath.exists()) {
			folderPath.mkdirs();
		}
		
		String fileType = imageFile.getContentType();
		String fileNo = UuidUtils.getUuidOnlyString(); // 저장파일명
		String fileNm = imageFile.getOriginalFilename();  // 원시파일명

		String fileExt = fileNm.substring(fileNm.lastIndexOf(".") + 1); // 파일확장자
		if (fileExt.length() < 3 || fileExt.length() > 4) {
			throw new CmmnBizException("업로드가 불가한 파일입니다.");
		}

		String[] fileExtArry = {"jpeg","jpg","png","gif"};
		
		if(!ArrayUtils.contains(fileExtArry, fileExt.toLowerCase())) {
			throw new CmmnBizException("확장자(" + fileExt + ")파일은 업로드가 불가합니다.");
		}

		long fileSize = imageFile.getSize(); // 파일크기
		long uploadMaxSize = cmmnProperties.getFileMng().getUploadMaxSize(); // 파일업로드 최대사이즈
		if (fileSize > uploadMaxSize) {
			throw new CmmnBizException(
					new StringBuilder()
					.append("업로드 파일크기가 ")
					.append(uploadMaxSize/1000000)
					.append("MByte를 초과할 수 없습니다.")
					.toString()
				);
		}

		String fileFullPath = new StringBuilder()
				.append(filePath)
				.append(File.separator)
				.append(fileNo)
				.toString();
		File file = new File(fileFullPath);

		try {
			imageFile.transferTo(file);
		} catch (IOException e) {
			throw new CmmnBizException("파일업로드를 실패했습니다.", e);
		}

		// 이미지파일의 썸네일 base64 스트링 추출하기
		String thumbnailBase64 = CoreUtils.convertImg2Base64ReSize(file, 150);

		FileInfoDto fileInfoDto = FileInfoDto.builder()
				.fileNo(fileNo)
				.fileGrpNo("-")
				.fileNm(fileNm)
				.fileExt(fileExt)
				.filePath(filePath)
				.fileSize(fileSize)
				.rm(thumbnailBase64)
				.fileType(fileType)
				.delYn("N")
				.crtr(RequestUtils.getUser().getMemId())
				.amdr(RequestUtils.getUser().getMemId())
				.build();

		fileService.insertFileInfo(fileInfoDto);

		return fileInfoDto;
	}

	/**
	 * 이미지파일 출력하기
	 * @param fileNo
	 * @return
	 */
	public ResponseEntity<byte[]> exportImgFile(String fileNo) {
		FileInfoDto fileInfoDto = fileService.getFileInfo(fileNo);
		String fileFullPath = new StringBuilder()
				.append(fileInfoDto.getFilePath())
				.append(File.separator)
				.append(fileNo)
				.toString();
		File file = new File(fileFullPath);

		try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);

			// 한글명 인코딩
	    	String encodedFileName = URLEncoder
						    	 		.encode(fileInfoDto.getFileNm(), StandardCharsets.UTF_8.toString())
						    	 		.replaceAll("\\+", "%20"); // 공백 처리

	    	// 출력 헤더셋팅
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType(fileInfoDto.getFileType()));
	        headers.setContentDispositionFormData("attachment", encodedFileName);

	        // 출력
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(fileBytes);
        } catch (IOException e) {
        	throw new CmmnBizException(e.getMessage(), e);
        }
	}

	/**
	 * 이미지 base64 스트링 출력하기
	 * @param fileNo
	 * @return
	 */
	public ImgBase64Dto exportImgBase64(String fileNo) {
		FileInfoDto fileInfoDto = fileService.getFileInfo(fileNo);
		String fileFullPath = new StringBuilder()
				.append(fileInfoDto.getFilePath())
				.append(File.separator)
				.append(fileNo)
				.toString();
		File file = new File(fileFullPath);

		return ImgBase64Dto.builder()
				.base64Data(CoreUtils.convertImg2Base64(file))
				.fileExt(fileInfoDto.getFileExt())
				.build();
	}
}
