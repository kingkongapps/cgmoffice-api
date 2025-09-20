package com.cgmoffice._sample.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice._sample.dto.SampleFileDto;
import com.cgmoffice._sample.dto.TestFileUpDto;
import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SampleFileMngService {
	private final AppDao appDao;
	private final FileService utilsFileService;

	public void fileUp(TestFileUpDto testFileUpDto) {

		String uploadRelativePath = "/sample";
		
		MultipartFile attachFile = testFileUpDto.getAttachFile();

		// 파일을 업로드한다.
		FileInfoDto fileInfoDto = utilsFileService.uploadFile("SMPL", null, attachFile, uploadRelativePath);

		CmmnMap params = new CmmnMap()
				.put("memo", testFileUpDto.getMemo())
				.put("fileInfo", fileInfoDto.getFileNo())
				;
		appDao.insert("sample.sampleFileMng.setInfo", params);
	}

	public void delete(String idx) {

		SampleFileDto info = appDao.selectOne("sample.sampleFileMng.getInfo", idx);

		String fileNo = info.getFileNo();
		
		// 파일을 삭제한다.
		utilsFileService.delete(fileNo);

		appDao.delete("sample.sampleFileMng.delInfo", idx);
	}

	public List<SampleFileDto> getList() {
		return appDao.selectList("sample.sampleFileMng.getList");
	}

}
