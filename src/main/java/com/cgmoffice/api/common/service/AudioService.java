package com.cgmoffice.api.common.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Throwables;
import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.RequestUtils;
import com.cgmoffice.core.utils.UuidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioService {
	private final FileService utilsFileService;
	private final CmmnProperties cmmnProperties;

	/**
	 *
	 * @param audioFile 오디오파일
	 * @param thumnailImg 썸네일파일
	 * @param uploadRelativePath 저장할 상대경로
	 * @return
	 */
	public FileInfoDto uploadAudio(
			MultipartFile audioFile,  // 오디오파일
			MultipartFile thumnailImg,  // 썸네일파일
			String uploadRelativePath  // 저장할 상대경로
			) {

		String filePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(uploadRelativePath)
				.toString(); // 파일저장경로
		File folderPath = new File(filePath);
		if (!folderPath.exists()) {
			folderPath.mkdirs();
		}

		String fileType = audioFile.getContentType();
		String fileNo = UuidUtils.getUuidOnlyString(); // 저장파일명
		String fileNm = audioFile.getOriginalFilename();  // 원시파일명

		String fileExt = fileNm.substring(fileNm.lastIndexOf(".") + 1); // 파일확장자
		if (fileExt.length() < 3 || fileExt.length() > 4) {
			throw new CmmnBizException("업로드가 불가한 파일입니다.");
		}

		if(!"mp3".equalsIgnoreCase(fileExt)) {
			throw new CmmnBizException("mp3 파일만 업로드가 가능합니다.");
		}

		long fileSize = audioFile.getSize(); // 파일크기
		long uploadMaxSize = cmmnProperties.getFileMng().getUploadAudioMaxSize(); // 오디오 파일업로드 최대사이즈
		if (fileSize > uploadMaxSize) {
			throw new CmmnBizException(
					new StringBuilder()
					.append("오디오 업로드 파일크기가 ")
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
			audioFile.transferTo(file);
		} catch (IOException e) {
			throw new CmmnBizException("파일업로드를 실패했습니다.", e);
		}

        String thumbnailBase64 = "";
		if(thumnailImg != null) {
			// 임시 파일 생성
	        File thumbnailFile = new File(
		        		new StringBuilder()
		        		.append(System.getProperty("java.io.tmpdir"))
		        		.append('/')
		        		.append(thumnailImg.getOriginalFilename())
		        		.toString()
	        		);

	        // MultipartFile을 File로 변환
	        try {
				thumnailImg.transferTo(thumbnailFile);
				// 썸네일 base64 스트링 추출하기
				thumbnailBase64 = CoreUtils.convertImg2Base64ReSize(thumbnailFile, 100);
				// 임시파일 제거
				thumbnailFile.delete();
			} catch (IllegalStateException | IOException e) {
				log.error(Throwables.getStackTraceAsString(e));
			}
		}

		FileInfoDto fileInfoDto = FileInfoDto.builder()
				.fileNo(fileNo)
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

		utilsFileService.insertFileInfo(fileInfoDto);

		return fileInfoDto;
	}

}
