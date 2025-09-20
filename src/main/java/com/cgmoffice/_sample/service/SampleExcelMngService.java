package com.cgmoffice._sample.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice._sample.dto.SampleExcelDnDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.utils.ExcelUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SampleExcelMngService {

	private final AppDao appDao;


	public byte[] createExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("예제 Sheet");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Age");

            // 데이터 추가
            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(1);
            dataRow.createCell(1).setCellValue("앨리스");
            dataRow.createCell(2).setCellValue(30);

            // OutputStream에 작성
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw new CmmnBizException("Excel 파일 생성 중 오류 발생", e);
        }
    }


	public ResponseEntity<byte[]> download02(SampleExcelDnDto dto) {
		List<Map<String, Object>> dataList = appDao.selectList("sample.sampleExcelMng.getList", dto.getUserName());

		return ExcelUtils.createExel(dto.getExcelDownConfig(), dataList);
	}


	public List<List<String>> read(MultipartFile excelfile) {
		return ExcelUtils.readExcel(excelfile);
	}

}
