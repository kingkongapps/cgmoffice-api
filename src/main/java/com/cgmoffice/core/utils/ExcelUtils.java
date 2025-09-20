package com.cgmoffice.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cgmoffice.api.common.dto.ExcelDownConfigDto;
import com.cgmoffice.api.common.dto.ExcelDownConfigDto.ColumnInfo;
import com.cgmoffice.core.exception.CmmnBizException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ExcelUtils {

	public ResponseEntity<byte[]> createExel(ExcelDownConfigDto excelDownConfig, List<Map<String, Object>> dataList) {

		try (Workbook workbook = new XSSFWorkbook()) {
			String sheetName = excelDownConfig.getSheetName();
			Sheet worksheet = workbook.createSheet(sheetName);
			List<ColumnInfo> colInfoList = excelDownConfig.getColInfoList();

			// 엑셀 셀 스타일
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

			int cellCnt;
			int rowCnt = -1;

			// 엑셀 컬럼넓이 셋팅
			cellCnt = -1;
			for(ColumnInfo colinfo: colInfoList) {
				worksheet.setColumnWidth(++cellCnt, colinfo.getWidth());
			}

			// 엑셀 컬럼제목 넣기
			cellCnt = -1;
			Row firstRow = worksheet.createRow(++rowCnt);
			for(ColumnInfo colinfo: colInfoList) {
				Cell cell = firstRow.createCell(++cellCnt);
				cell.setCellValue(colinfo.getExelColNm());
				cell.setCellStyle(headerCellStyle);
			}

			// 엑셀 데이터 셋팅하기
			for(Map<String, Object> data : dataList) {
				// 엑셀 row 생성
				Row row = worksheet.createRow(++rowCnt);

				cellCnt = -1;
				for(ColumnInfo colinfo: colInfoList) {
					// 데이터 컬럼명 추출
					String dataColNm = colinfo.getDataColNm();
					// 데이터 추출
					Object value = data.get(dataColNm);
					// 엑셀 row 의 cell 생성
					Cell cell = row.createCell(++cellCnt);

					// cell 에 데이터 셋팅
					if(value instanceof Number) {
						cell.setCellValue(((Number)value).doubleValue());
					} else if(value instanceof String) {
						cell.setCellValue((String)value);
					} else {
						cell.setCellValue(value.toString());
					}
				}
			}

			// 엑셀 출력데이터 생성
			byte[] excelData;
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				workbook.write(out);
				excelData = out.toByteArray();
			}

			// 한글명 인코딩
	    	String encodedFileName = URLEncoder
						    	 		.encode(excelDownConfig.getFileName(), StandardCharsets.UTF_8.toString())
						    	 		.replaceAll("\\+", "%20"); // 공백 처리

	    	// 출력 헤더셋팅
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        headers.setContentDispositionFormData("attachment", encodedFileName);

	        // 출력
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(excelData);
		} catch (IOException e) {
            throw new CmmnBizException("Excel 파일 생성 중 오류 발생", e);
		}
	}

	public List<List<String>> readExcel(MultipartFile file) {
		List<List<String>> data = new ArrayList<>();

        try (InputStream is = file.getInputStream();
        		Workbook workbook = new XSSFWorkbook(is)
        				) {
        	// 첫번째 시트 추출
            Sheet sheet = workbook.getSheetAt(0);

            // 추출된 시트에서 row 를 추출
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                
                int lastCellNum = row.getLastCellNum(); // 해당 row에서 실제 마지막 column index + 1

                for (int i = 0; i < lastCellNum; i++) {
                    // 빈 셀도 포함해서 가져오기
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    String value = getCellValue(cell);
                    rowData.add(value);
                }

                data.add(rowData);
            }
        } catch (IOException e) {
            throw new CmmnBizException("Excel 파일 처리 중 오류 발생", e);
        }

        return data;
    }

    private String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getDateCellValue().toString();
                    // 날짜 포맷 지정
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    return sdf.format(cell.getDateCellValue());
                } else {
                	double value = cell.getNumericCellValue();
                    // 정수인지 판별
                    if (value == Math.floor(value)) {
                        return String.valueOf((long) value); // 소수점 제거
                    } else {
                        return String.valueOf(value); // 그대로 출력
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
