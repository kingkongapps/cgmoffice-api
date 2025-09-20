package com.cgmoffice.api.cnt.service;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfwriter.compress.CompressParameters;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.cgmoffice.api.cnt.dto.IndvClusMergeaddInfDto;
import com.cgmoffice.api.cnt.dto.IndvClusRcvDtlDto;
import com.cgmoffice.api.cnt.dto.IndvClusRcvMstDto;
import com.cgmoffice.api.cnt.dto.PdfAutoSplitDto;
import com.cgmoffice.api.cnt.dto.PdfInfoDto;
import com.cgmoffice.api.cnt.dto.PdfMergeDto;
import com.cgmoffice.api.cnt.dto.PdfPreviewDto;
import com.cgmoffice.api.cnt.dto.PdfSplitDto;
import com.cgmoffice.api.cnt.dto.PdfTocDto;
import com.cgmoffice.api.cnt.dto.PdfTocDto.TocContent;
import com.cgmoffice.api.cnt.dto.PrdInfoDto;
import com.cgmoffice.api.cnt.dto.PrdInfoDto.PrdInfoChgLstDto;
import com.cgmoffice.api.cnt.dto.PrdtClusDto;
import com.cgmoffice.api.cnt.dto.SearchPrdClusPdfDto;
import com.cgmoffice.api.cnt.dto.TocEntryDto;
import com.cgmoffice.api.common.dto.FileInfoDto;
import com.cgmoffice.api.common.service.FileService;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.domain.PageList;
import com.cgmoffice.core.datasource_utils.mybatis.paginator.vo.PageConfig;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.JsonUtils;
import com.cgmoffice.core.utils.PDFResourceOptimizer;
import com.cgmoffice.core.utils.RequestUtils;
import com.cgmoffice.core.utils.UuidUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrdtClusPdfMngService {

	private final AppDao appDao;
	private final CmmnProperties cmmnProperties;
	private final FileService commonFileService;
	private final PrdtClusMngService prdtClusMngService;
	private final PrdtMngService prdtMngService;
	private final HttpServletRequest request;
	private String strSndurl; // 유입경로 Url req.getHeader("Referer")가 null인 경우가 있기 때문이다.
	private String[] regexList = {
			"^\\s*\\d*\\.?\\s*(.+?)\\s*[·\\.\\-‧∙ㆍ\\s]{3,}\\s*(\\d{1,4})\\s*$",
			"^(\\d{1,3})\\s*[·\\.\\-‧∙ㆍ\\s]{3,}\\s*([가-힣a-zA-Z0-9\\s\\-()\\[\\]/,.·∙ㆍ‧]+)$",
			//"^(.*?)\\s{2,}(\\d+)$",
			"^([^\\n/]+?)\\s{1,}(\\d{1,3})$",
			"^\\s*(.+?)\\s*[·\\.\\-\\s]{3,}\\s*(\\d{1,4})\\s*$",
	};

	/**
	 * 임시작업폴더 생성
	 * @return
	 */
	private String createTmpFolder() {
		String taskDir = new StringBuilder()
				.append("temp_")
				.append(CoreUtils.genTimestampUniqId())
				.toString();

		// 저장할 경로 생성
		new File(
				new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
				.append(taskDir)
				.toString())
		.mkdirs();

		return taskDir;
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

	/**
	 * 목차페이지 생성작업
	 * @param pullFilePath
	 * @return
	 */
	private File createTOCPage(String pullFilePath, PdfTocDto pdfTocDto) {

		File taskFile = new File(pullFilePath);
		String tocFilePath = "toc_temp.pdf";

		ClassPathResource fontResource = new ClassPathResource("static/font/NanumGothic.ttf");

		try (
			PDDocument sourceDoc = Loader.loadPDF(taskFile);
			PDDocument tocDoc = new PDDocument();
			InputStream fontStream = fontResource.getInputStream()
		) {
			// 폰트 및 페이지 정보
			PDType0Font font = PDType0Font.load(tocDoc, fontStream);
			List<TocContent> tocList = pdfTocDto.getTocContentList();
			if (tocList == null || tocList.isEmpty()) {
				throw new IllegalArgumentException("TOC 리스트가 비어 있습니다.");
			}

			PDPage samplePage = sourceDoc.getPage(0);
			PDRectangle pageSize = samplePage.getMediaBox();
			float pageHeight = pageSize.getHeight();
			float pageWidth = pageSize.getWidth();

			// 레이아웃 설정
			int fontSize = 12;
			float lineSpacing = fontSize + 6;
			float topMargin = 100f;
			float bottomMargin = 100f;
			float leftMargin = 70f;
			float rightMargin = 70f;

			float usableHeight = pageHeight - topMargin - bottomMargin;
			int maxLinesPerPage = (int) (usableHeight / lineSpacing);
			int totalPages = (tocList.size() + maxLinesPerPage - 1) / maxLinesPerPage;

			for (int pageNum = 0; pageNum < totalPages; pageNum++) {
				PDPage importedPage = tocDoc.importPage(samplePage);

				try (PDPageContentStream contentStream = new PDPageContentStream(tocDoc, importedPage, AppendMode.APPEND, true)) {

					// 배경 흰색
					contentStream.setNonStrokingColor(1f);
					contentStream.addRect(0, 0, pageWidth, pageHeight);
					contentStream.fill();

					// 제목 출력
					contentStream.beginText();
					contentStream.setFont(font, 20);
					contentStream.setNonStrokingColor(0f);
					contentStream.newLineAtOffset(leftMargin, pageHeight - 70);
					contentStream.showText(pdfTocDto.getTitle());
					contentStream.endText();

					// 목차 본문
					int start = pageNum * maxLinesPerPage;
					int end = Math.min(start + maxLinesPerPage, tocList.size());
					float currentY = pageHeight - topMargin;

					for (int i = start; i < end; i++) {
						TocContent content = tocList.get(i);
						String pageStr = String.valueOf(content.getPage());

						// 페이지 번호 너비
						float pageStrWidth = font.getStringWidth(pageStr) / 1000 * fontSize;
						float pageX = pageWidth - rightMargin - pageStrWidth;

						// 제목 너비 제한 (페이지번호 영역 제외)
						float maxTitleWidth = pageX - leftMargin - 10;
						String trimmedTitle = trimTextToFit(content.getTitle(), font, fontSize, maxTitleWidth);

						// 제목 출력 (왼쪽)
						contentStream.beginText();
						contentStream.setFont(font, fontSize);
						contentStream.setNonStrokingColor(0f);
						contentStream.newLineAtOffset(leftMargin, currentY);
						contentStream.showText(trimmedTitle);
						contentStream.endText();

						// 페이지 번호 출력 (오른쪽)
						contentStream.beginText();
						contentStream.setFont(font, fontSize);
						contentStream.newLineAtOffset(pageX, currentY);
						contentStream.showText(pageStr);
						contentStream.endText();

						currentY -= lineSpacing;
					}
				}
			}

			tocDoc.save(tocFilePath, new CompressParameters(2000));

		} catch (IOException e) {
			throw new CmmnBizException("목차페이지 생성작업 중 오류 발생.", e);
		}

		return new File(tocFilePath);
	}

	private String trimTextToFit(String text, PDFont font, float fontSize, float maxWidth) throws IOException {
		String ellipsis = "...";
		float ellipsisWidth = font.getStringWidth(ellipsis) / 1000 * fontSize;

		for (int i = text.length(); i > 0; i--) {
			String sub = text.substring(0, i);
			float width = font.getStringWidth(sub) / 1000 * fontSize;
			if (width + ellipsisWidth <= maxWidth) {
				return sub + ellipsis;
			}
		}
		return ellipsis;
	}

	/**
	 * PDF 파일의 페이지를 셋팅하는 작업
	 * @param pdfFile  작업대상 PDF 파일
	 * @param height  페이지를 표시하기 위한 pdf 하단에서의 높이
	 */
	private String setPdfPage(
			String fullFilePath,
			float barHeight  // PDFBox에서는 points 단위, 1pt = 1/72 inch
			) {

		File pdfFile = new File(fullFilePath);

		if(!pdfFile.exists()) {
			return null;
		}

		String rsltStr = fullFilePath + "_final";

		// 원본 PDF 로드
		try(PDDocument document = Loader.loadPDF(pdfFile)){
			// pdf 파일의 페이지수를 추출한다.
			int pageCount = document.getNumberOfPages();

			for (int i = 0; i < pageCount; i++) {
				PDPage page = document.getPage(i);
				PDRectangle mediaBox = page.getMediaBox();
				float pageWidth = mediaBox.getWidth();
				// float pageHeight = mediaBox.getHeight();

				try (PDPageContentStream contentStream = new PDPageContentStream(
						document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

					// 하얀색 직사각형 그리기 (하단)
					contentStream.setNonStrokingColor(1f); // 흰색
					contentStream.addRect(
								0, // The lower left x coordinate
								0, // The lower left y coordinate.
								pageWidth, // The width of the rectangle.
								barHeight  // The height of the rectangle.
							);
					contentStream.fill();

					// 페이지 번호 텍스트 설정
					String pageText = String.format(
								"%d / %d", // String Format
								i + 1,	 // 현재 페이지
								pageCount  // 총 페이지수
							);
					contentStream.beginText();
					contentStream.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 10); // 폰트와 크기 설정
					contentStream.setNonStrokingColor(0f); // 검정색

					// 텍스트 위치 (가운데 정렬)
					float textWidth = new PDType1Font(FontName.HELVETICA_BOLD).getStringWidth(pageText) / 1000 * 8;
					float textX = (pageWidth - textWidth) / 2;
					float textY = (barHeight - 8) / 2 + 2; // 바 높이 중앙, 약간 위로 조정

					contentStream.newLineAtOffset(textX, textY);
					contentStream.showText(pageText);
					contentStream.endText();
				}
			}

			// 덮어쓰기 저장
			document.save(rsltStr, new CompressParameters(2000));
			return rsltStr;
		} catch (IOException e) {
			throw new CmmnBizException("PDF 파일의 페이징작업중 오류발생.", e);
		}
	}


	public ResponseEntity<Resource> splitZipDn(MultipartFile pdfSplitZipDnFile) {

		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 임시작업폴더 생성한다.
		String taskDir = createTmpFolder();

		// 파일명 추출
		String orgFilenmPrefix = StringUtils.defaultString(pdfSplitZipDnFile.getOriginalFilename(), "PDF_FILE.pdf");
		orgFilenmPrefix = orgFilenmPrefix.substring(0, orgFilenmPrefix.length() - 4);

		// 임시파일 생성한다.
		File tmpTaskFile = null;

		List<String> outputFileFullPathList = new ArrayList<String>();
		try {
			tmpTaskFile = File.createTempFile("pdf-", UuidUtils.getUuidOnlyString());

			// 첨부한 pdf 파일을 임시파일로 복사한다.
			pdfSplitZipDnFile.transferTo(tmpTaskFile);

			// 원본 PDF 로드
			try(PDDocument document = Loader.loadPDF(tmpTaskFile)){

				// pdf 파일의 페이지수를 추출한다.
				int pageCount = document.getNumberOfPages();

				// Splitter 객체 생성
				Splitter splitter = new Splitter();

				// 페이지별로 쪼개진 PDF 문서 목록을 반환한다.
				List<PDDocument> pages = splitter.split(document);

				for (int i = 0; i < pageCount; i++) {
					// pdf 문서의 각 페이지별 문서를 추출한다.
					try(PDDocument pageDoc = pages.get(i)){

						// 쪼개진 문서를 실제저장소에 저장한 문서명을 생성한다.
						String tmpFileNm = UuidUtils.getUuidOnlyString();

						String outputFileFullPath = new StringBuilder()
								.append(cmmnProperties.getFileMng().getUploadRootPath())
								.append(File.separator)
								.append(taskDir)
								.append(File.separator)
								.append(tmpFileNm)
								.toString();
						// 쪼개진 문서를 실제저장소에 저장한다.
						pageDoc.save(outputFileFullPath, new CompressParameters(2000));

						outputFileFullPathList.add(outputFileFullPath);
					}
				}
			}

			// 압축처리할 파일경로
			String zipFilePath = new StringBuilder()
					.append(cmmnProperties.getFileMng().getUploadRootPath())
					.append(File.separator)
					.append(taskDir)
					.append(File.separator)
					.append(UuidUtils.getUuidOnlyString())
					.toString();

			// 압축처리할 파일
			File zipFile = new File(zipFilePath);
			try(FileOutputStream fos = new FileOutputStream(zipFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					ZipOutputStream zos = new ZipOutputStream(bos)){

				int cnt = 0;
				for(String path : outputFileFullPathList) {
					File itemFile = new File(path);
					try (FileInputStream fis = new FileInputStream(itemFile)) {
						cnt++;

						String itemFileNm = new StringBuilder()
								.append(orgFilenmPrefix)
								.append("_")
								.append(StringUtils.leftPad(Integer.toString(cnt), 4, "0"))
								.append(".pdf")
								.toString();

						ZipEntry zipEntry = new ZipEntry(itemFileNm);
						zos.putNextEntry(zipEntry);

						byte[] buffer = new byte[1024];
						int length;
						while ((length = fis.read(buffer)) != -1) {
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
					}
				}
			}

			Resource resource = new FileSystemResource(zipFile);

			// 한글명 인코딩
			String encodedFileName = URLEncoder
								 		.encode(orgFilenmPrefix + ".zip", StandardCharsets.UTF_8.toString())
								 		.replaceAll("\\+", "%20"); // 공백 처리

			// 출력 헤더셋팅
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.valueOf("application/zip"));
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", encodedFileName);

			// 출력
			return ResponseEntity.ok()
					.headers(headers)
					.body(resource);

		} catch (IOException e) {
			throw new CmmnBizException("파일분할 다운로드중 오류가 발생", e);
		}
	}

	/**
	 * pdf 페이지별 분리작업
	 * @param dto
	 * @return
	 */
	public PdfSplitDto split(PdfSplitDto dto) {

		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 임시작업폴더 생성한다.
		String taskDir = createTmpFolder();
		dto.setTaskDir(taskDir);

		File tmpTaskFile = null;
		try {
			// 첨부한 pdf파일을 추출한다.
			MultipartFile inputPdf = dto.getInputPdf();
			// 임시파일 생성한다.
			tmpTaskFile = File.createTempFile("pdf-", UuidUtils.getUuidOnlyString());
			// 첨부한 pdf 파일을 임시파일로 복사한다.
			inputPdf.transferTo(tmpTaskFile);

			PDFTextStripper stripper = new PDFTextStripper();

			int firstPage = 0;
			String isMainPage = "N", isFirst = "N", isLast = "N";

			// 원본 PDF 로드
			try(PDDocument document = Loader.loadPDF(tmpTaskFile)){
				// pdf 파일의 페이지수를 추출한다.
				int pageCount = document.getNumberOfPages();

				// Splitter 객체 생성
				Splitter splitter = new Splitter();

				// 페이지별로 쪼개진 PDF 문서 목록을 반환한다.
				List<PDDocument> pages = splitter.split(document);

				List<PdfPreviewDto> previewList = new ArrayList<PdfPreviewDto>();
				for (int i = 0; i < pageCount; i++) {
					// 목차리스트 결과 출력
					List<TocEntryDto> tocEntries = new ArrayList<>();

					// pdf 문서의 각 페이지별 문서를 추출한다.
					try(PDDocument pageDoc = pages.get(i)) {
						// 3) 전체 페이지 돌면서 목차 항목 찾기 [선택한 목차 페이지만]
						for(String regex : regexList) {
							int cnt = 0;
							if("Y".equals(isFirst) && "Y".equals(isLast)) break;
							// 정규식: 번호 + 점선 + 한글/영문 혼합 제목
							Pattern tocPattern = Pattern.compile(regex);
							int plusPage = i+1;
							stripper.setStartPage(plusPage);
							stripper.setEndPage(plusPage);
							String text = stripper.getText(document);
							String[] lines = text.split("\\r?\\n");

							for (String line : lines) {
								Matcher m = tocPattern.matcher(line.trim());
								if (m.matches()) {
									String number = "";
									String title = "";

									String trimText = text.trim().replaceAll("\\s+", "").replaceAll("\u00A0", "");

									//목차가 발견된 페이지가 최초이다.
									if(trimText.contains("목차")) {
										isMainPage = "Y";
										isFirst = "Y";
									} else if(trimText.contains("색인")) {
										break;
									}

									//1751251713353.pdf
									if("^(.+?)\\s{1,}(\\d{1,4})$".equals(regex)) {
										number = m.group(2);
										title = m.group(1).trim().replace("\u00A0", "");
										if(firstPage == 0) firstPage = plusPage - 1;
										tocEntries.add(new TocEntryDto(number, title, "Y", regex, plusPage, 0, firstPage, pageCount, cnt++));
										log.info("목차 발견: {} {} (페이지 {})\n", number, title, plusPage);
									//sh뉴파워플랜종신공제
									} else if("^\\s*\\d*\\.?\\s*(.+?)\\s*[·\\.\\-‧∙ㆍ\\s]{3,}\\s*(\\d{1,4})\\s*$".equals(regex)) {
										number = m.group(2);
										title = m.group(1).trim().replace("\u00A0", "");
										if(firstPage == 0) firstPage = plusPage - 1;
										tocEntries.add(new TocEntryDto(number, title, "Y", regex, plusPage, 0, firstPage, pageCount, cnt++));
										log.info("목차 발견: {} {} (페이지 {})\n", number, title, plusPage);
									//(무)하나원큐연금저축보험, 778페이지 원본
									} else if("^(\\d{1,3})\\s*[·\\.\\-‧∙ㆍ\\s]{3,}\\s*([가-힣a-zA-Z0-9\\s\\-()\\[\\]/,.·∙ㆍ‧]+)$".equals(regex)) {
										number = m.group(1);
										title = m.group(2).trim().replace("\u00A0", "");
										if(firstPage == 0) firstPage = plusPage - 1;
										tocEntries.add(new TocEntryDto(number, title, "Y", regex, plusPage, 0, firstPage, pageCount, cnt++));
										log.info("목차 발견: {} {} (페이지 {})\n", number, title, plusPage);
									//한화생명 경영인H정기보험
									} else if("^([^\\n/]+?)\\s{1,}(\\d{1,3})$".equals(regex)) {
										number = m.group(2);
										title = m.group(1).trim().replace("\u00A0", "");
										if(firstPage == 0) firstPage = plusPage - 1;
										tocEntries.add(new TocEntryDto(number, title, "Y", regex, plusPage, 0, firstPage, pageCount, cnt++));
										log.info("목차 발견: {} {} (페이지 {})\n", number, title, plusPage);
									}
								}
							}
						}

						if("Y".equals(isFirst) && tocEntries.size() == 0) {
							if(tocEntries.size() == 0) {
								isLast = "Y";
								isMainPage = "N";
							}
						}

						// 쪼개진 문서를 실제저장소에 저장한 문서명을 생성한다.
						String tmpFileNm = UuidUtils.getUuidOnlyString();

						String outputFileFullPath = new StringBuilder()
								.append(cmmnProperties.getFileMng().getUploadRootPath())
								.append(File.separator)
								.append(taskDir)
								.append(File.separator)
								.append(tmpFileNm)
								.toString();
						// 쪼개진 문서를 실제저장소에 저장한다.
						pageDoc.save(outputFileFullPath, new CompressParameters(2000));

						// 미리보기 썸네일 생성
						PDFRenderer renderer = new PDFRenderer(pageDoc); // 첫 페이지만 렌더링

						try {
							// 20dpi의 웹용 썸네일 생성
							BufferedImage image = renderer.renderImageWithDPI(
									0, // 첫페이지
									20 // dpi 값
									);
							try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
								ImageIO.write(image, "png", baos);
								byte[] imageBytes = baos.toByteArray();
								String previewBase64 = Base64.getEncoder().encodeToString(imageBytes);

								// 미리보기 썸네일값을 dto 에 셋팅
								PdfPreviewDto previewDto = PdfPreviewDto.builder()
										.sort(i)
										.previewBase64(previewBase64)
										.tmpFileNm(tmpFileNm)
										.isMainPage(tocEntries.size() > 0 && "Y".equals(isMainPage) ? "Y" : "N")
										.build();
								previewList.add(previewDto);
							}
						} catch(Exception e) {
							log.error(">>> pdf error: {}", Throwables.getStackTraceAsString(e));

							// 미리보기 썸네일값을 dto 에 셋팅
							PdfPreviewDto previewDto = PdfPreviewDto.builder()
									.sort(i)
									.previewBase64("")
									.tmpFileNm(tmpFileNm)
									.isMainPage(tocEntries.size() > 0 && "Y".equals(isMainPage) ? "Y" : "N")
									.build();
							previewList.add(previewDto);
							continue;
						}
					}
				}
				dto.setPreviewList(previewList);
			}
		} catch (IOException e) {
			throw new CmmnBizException("파일분할중 오류가 발생", e);
		} finally {
			if(tmpTaskFile != null) {
				tmpTaskFile.delete();
			}
		}

		return dto;
	}

	/**
	 * 실제 합본작업 진행
	 *
	 * @param fileFullpathList  합본대상 pdf 전체경로 목록
	 * @param savePath  합본pdf의 저장할 경로
	 * @param fileNm 합본pdf의 다운로드시 저장될 파일 이름
	 * @param fileNo 합본pdf의 서버의 실제저장경로에 저장할 이름
	 * @return
	 */
	public FileInfoDto mergePdf(List<String> fileFullpathList, String savePath, String fileNm, String fileNo) {

		fileNo = CoreUtils.normalizeNFC(fileNo);
		fileNm = CoreUtils.normalizeNFC(fileNm);

		// 합본pdf를 저장할 경로
		String outputPdfPath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(savePath)
				.toString();

		File outputPdfDir = new File(outputPdfPath);
		if(!outputPdfDir.exists()) {
			outputPdfDir.mkdirs();
		}


		PDFMergerUtility merger = new PDFMergerUtility();
		try {
			for(String path : fileFullpathList) {
				merger.addSource(new File(path));
			}



			// 2시간이전의 임시폴더는 모두 삭제한다.
			deleteOldTmpFolder();

			// 임시작업폴더 생성한다.
			String taskDir = createTmpFolder();

			// 합본pdf를 저장할 파일포함 full 경로
			String tmpFilePath = new StringBuilder()
					.append(cmmnProperties.getFileMng().getUploadRootPath())
					.append(File.separator)
					.append(taskDir)
					.append(File.separator)
					.append(UuidUtils.getUuidOnlyString())
					.append(".pdf")
					.toString();

			// 출력 파일 경로 설정
			merger.setDestinationFileName(tmpFilePath);
			// 병합 실행
			merger.mergeDocuments(null); // MemoryUsageSetting.defaultMemoryUsage() 대신 null도 가능

			try(PDDocument document = Loader.loadPDF(new File(tmpFilePath))) {

				// PDF 리소스 최적화 실행
				PDFResourceOptimizer optimizer = new PDFResourceOptimizer();
				optimizer.optimizeResources(document);
				optimizer.printOptimizationStats();


				String mergedFilePath = new StringBuilder()
						.append(outputPdfPath)
						.append(File.separator)
						.append(fileNo)
						.toString();

				// 최적화된 문서 저장
				document.save(mergedFilePath);

				// DB에 파일정보 저장
				FileInfoDto fileInfoDto = FileInfoDto.builder()
						.fileNo(fileNo)
						.fileNm(fileNm)
						.fileGrpNo("-")
						.fileGb("-")
						.fileExt("pdf")
						.filePath(outputPdfPath)
						.fileSize(-1)
						.fileType("application/pdf")
						.delYn("N")
						.rm("CLUS")
						.crtr(RequestUtils.getUser().getMemId())
						.amdr(RequestUtils.getUser().getMemId())
						.build();
				commonFileService.insertFileInfo(fileInfoDto);

				// 상품별 파일정보 확인 및 업데이트
				setClusFileInfo(fileInfoDto);

				return fileInfoDto;
			}

		} catch (IOException e) {
			throw new CmmnBizException("pdf 파일합본작업중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 합본처리
	 * @param dto
	 * @return
	 */
	public PdfMergeDto mergeServer(@Valid PdfMergeDto dto) {

		List<String> fileNmList = dto.getFileNmList();
		String taskDir = dto.getTaskDir();
		String saveFileNm = new StringBuilder()
				.append(dto.getSaveFileNm())
				.append(".pdf")
				.toString();

		// 합본대상 pdf 전체경로 목록작성
		List<String> fileFullpathList = new ArrayList<String>();
		for(String fileNm : fileNmList) {
			String fileFullPath = new StringBuilder()
					.append(cmmnProperties.getFileMng().getUploadRootPath())
					.append(File.separator)
					.append(taskDir)
					.append(File.separator)
					.append(fileNm)
					.toString();
			fileFullpathList.add(fileFullPath);
		}

		// 합본pdf의 서버의 실제저장경로에 저장할 이름
		String fileNo = new StringBuilder()
				.append("CUS_")
				.append(saveFileNm.substring(0, saveFileNm.lastIndexOf(".")))
				.append('_')
				.append(CoreUtils.genTimestampUniqId())
				.append('.')
				.append("pdf")
				.toString();

		dto.setFileNo(fileNo);

		// 실제 합본작업 진행
		FileInfoDto mergedFileInfo =
				mergePdf(
					fileFullpathList,  // 합본대상 pdf 전체경로 목록
					cmmnProperties.getPdfDir(), // 합본pdf의 저장할 경로
					saveFileNm,  // 합본pdf의 다운로드시 저장될 파일 이름
					fileNo  // 합본pdf의 서버의 실제저장경로에 저장할 이름
				);
		log.debug(">>> mergedFileInfo: {}", JsonUtils.toJsonStrPretty(mergedFileInfo));

		return dto;
	}

	// 목차 리스트 추출
	public List<TocEntryDto> getTocList(@Valid PdfMergeDto dto) {

		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 임시작업폴더 생성한다.
		String taskDir = createTmpFolder();
		dto.setTaskDir(taskDir);

		File tmpTaskFile = null;

		// 목차리스트 결과 출력
		List<TocEntryDto> tocEntries = new ArrayList<>();

		try {
			// 첨부한 pdf파일을 추출한다.
			MultipartFile inputPdf = dto.getInputPdf();
			// 임시파일 생성한다.
			tmpTaskFile = File.createTempFile("pdf-", UuidUtils.getUuidOnlyString());
			// 첨부한 pdf 파일을 임시파일로 복사한다.
			inputPdf.transferTo(tmpTaskFile);

			//목차 페이지 여부
			String isMainPage = "N";

			try (PDDocument document = Loader.loadPDF(tmpTaskFile)) {
				int totalPages = document.getNumberOfPages();

				PDFTextStripper stripper = new PDFTextStripper();

				log.info("📌 목차 항목과 페이지 매핑:");

				int firstPage = 0;

				List<String> selPages = dto.getPageList();

				// 3) 전체 페이지 돌면서 목차 항목 찾기 [선택한 목차 페이지만]
				for(String regex : regexList) {
					int cnt = 0;
					if("Y".equals(isMainPage)) break;
					// 정규식: 번호 + 점선 + 한글/영문 혼합 제목
					Pattern tocPattern = Pattern.compile(regex);
					for (int page = 1; page <= totalPages; page++) {
						for(String selPage : selPages) {
							if(Integer.parseInt(selPage.replaceAll("[^0-9]", "")) == page) {
//								stripper.setSortByPosition(true);
//								stripper.setWordSeparator(" ");               // 단어 사이에 공백 삽입
//								stripper.setLineSeparator("\n");              // 줄 바꿈 문자 지정
								stripper.setStartPage(page);
								stripper.setEndPage(page);
								String text = stripper.getText(document);
								String[] lines = text.split("\\r?\\n");

								for (String line : lines) {
									Matcher m = tocPattern.matcher(line.trim());
									if (m.matches()) {
										String number = "";
										String title = "";

										if("N".equals(isMainPage)) isMainPage = "Y";

										//1751251713353.pdf
										if("^(.+?)\\s{1,}(\\d{1,4})$".equals(regex)) {
											number = m.group(2);
											title = m.group(1).trim().replace("\u00A0", "");
											if(firstPage == 0) firstPage = page - 1;
											tocEntries.add(new TocEntryDto(number, title, "Y", regex, page, 0, firstPage, totalPages, cnt++));
											log.info("목차 발견: {} {} (페이지 {})\n", number, title, page);
											//sh뉴파워플랜종신공제
										} else if("^\\s*\\d*\\.?\\s*(.+?)\\s*[·\\.\\-‧∙ㆍ\\s]{3,}\\s*(\\d{1,4})\\s*$".equals(regex)) {
											number = m.group(2);
											title = m.group(1).trim().replace("\u00A0", "");
											if(firstPage == 0) firstPage = page - 1;
											tocEntries.add(new TocEntryDto(number, title, "Y", regex, page, 0, firstPage, totalPages, cnt++));
											log.info("목차 발견: {} {} (페이지 {})\n", number, title, page);
										//(무)하나원큐연금저축보험, 778페이지 원본
										} else if("^(\\d{1,3})\\s*[·\\.\\-‧∙ㆍ\\s]{3,}\\s*([가-힣a-zA-Z0-9\\s\\-()\\[\\]/,.·∙ㆍ‧]+)$".equals(regex)) {
											number = m.group(1);
											title = m.group(2).trim().replace("\u00A0", "");
											if(firstPage == 0) firstPage = page - 1;
											tocEntries.add(new TocEntryDto(number, title, "Y", regex, page, 0, firstPage, totalPages, cnt++));
											log.info("목차 발견: {} {} (페이지 {})\n", number, title, page);
										//한화생명 경영인H정기보험
										} else if("^([^\\n/]+?)\\s{1,}(\\d{1,3})$".equals(regex)) {
											number = m.group(2);
											title = m.group(1).trim().replace("\u00A0", "");
											if(firstPage == 0) firstPage = page - 1;
											tocEntries.add(new TocEntryDto(number, title, "Y", regex, page, 0, firstPage, totalPages, cnt++));
											log.info("목차 발견: {} {} (페이지 {})\n", number, title, page);
										}
									}
								}
							}
						}
					}
				}
			} catch (IOException e) {
				throw new CmmnBizException("목차 리스트 추출 작업 중 오류가 발생했습니다.", e);
			}

		} catch (IOException e) {
			throw new CmmnBizException("파일분할중 오류가 발생", e);
		} finally {
			if(tmpTaskFile != null) {
				tmpTaskFile.delete();
			}
		}

		// 출력
		return tocEntries;
	}

	// PDF 자동분할 작업
	public ResponseEntity<Resource> autoSplitPdf(@Valid PdfAutoSplitDto dto) {
		// 첨부한 pdf파일을 추출한다.
		MultipartFile inputPdf = dto.getInputPdf();

		ObjectMapper mapper = new ObjectMapper();
		List<TocEntryDto> tocEntryDto = new ArrayList<TocEntryDto>();

		try {
			tocEntryDto = mapper.readValue(dto.getStrResList(), new TypeReference<List<TocEntryDto>>() {});
			log.debug(tocEntryDto.toString());
			// 1순위: startPage 오름차순, 2순위: idx 오름차순
			tocEntryDto.sort(Comparator.comparingInt(TocEntryDto::getStartPage).thenComparingInt(TocEntryDto::getIdx));
			// idx값 재세팅
			for(int i = 0; i < tocEntryDto.size(); i++) {
				tocEntryDto.get(i).setIdx(i);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// 합본pdf를 저장할 파일포함 full 경로
		String outputDirPath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.toString();

		// Path to your PDF file
		File outputDir = new File(outputDirPath);

		if (!outputDir.exists()) outputDir.mkdirs();

		//목차 페이지 여부
		String isMainPage = "N";

		Resource resource = null;
		//출력 헤더셋팅
		HttpHeaders headers = null;

		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 임시작업폴더 생성한다.
		String taskDir = createTmpFolder();
		dto.setTaskDir(taskDir);

		File tmpTaskFile = null;

		// 목차리스트 결과 출력
		List<TocEntryDto> tocEntries = new ArrayList<>();

		try {
			// 임시파일 생성한다.
			tmpTaskFile = File.createTempFile("pdf-", UuidUtils.getUuidOnlyString());
			// 첨부한 pdf 파일을 임시파일로 복사한다.
			inputPdf.transferTo(tmpTaskFile);

			try (PDDocument document = Loader.loadPDF(tmpTaskFile)) {
				int totalPages = document.getNumberOfPages();

				PDFTextStripper stripper = new PDFTextStripper();

				int firstPage = 0;

				List<String> selPages = dto.getPageList();

				// 3) 전체 페이지 돌면서 목차 항목 찾기 [선택한 목차 페이지만]
				for(String regex : regexList) {
					int cnt = 0;
					if("Y".equals(isMainPage)) break;
					// 정규식: 번호 + 점선 + 한글/영문 혼합 제목
					Pattern tocPattern = Pattern.compile(regex);
					for (int page = 1; page <= totalPages; page++) {
						for(String selPage : selPages) {
							if(Integer.parseInt(selPage.replaceAll("[^0-9]", "")) == page) {
								stripper.setStartPage(page);
								stripper.setEndPage(page);
								String text = stripper.getText(document);
								String[] lines = text.split("\\r?\\n");

								for (int i = 0; i < tocEntryDto.size(); i++) {
									for (String line : lines) {
										Matcher m = tocPattern.matcher(line.trim());
										if (m.matches()) {
											cnt++;
											TocEntryDto dtoObj = tocEntryDto.get(i);

											String number = "";
											String title = "";

											if("N".equals(isMainPage)) isMainPage = "Y";

											if((dtoObj.getIdx() + 1) == cnt) {
												number = dtoObj.getNumber();
												title = dtoObj.getTitle().trim().replace("\u00A0", "");
												if(firstPage == 0) firstPage = page - 1;
												tocEntries.add(new TocEntryDto(number, title, "Y", regex, dtoObj.getStartPage(), dtoObj.getEndPage(), firstPage, totalPages, cnt - 1));
												log.info("목차 추가 : {} {} (페이지 {})\n", number, title, page);
												log.info("cnt {}\n", cnt);
												break;
											}
										}
									}
								}
							}
						}
					}
				}

				// 4) 목차 기준으로 페이지 범위 나누기
				tocEntries.sort(Comparator.comparingInt(e -> e.idx));

				// 압축 결과 파일
				String zipFileName = dto.getSaveFileNm() + ".zip";

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

					for (int i = 0; i < tocEntries.size(); i++) {
						TocEntryDto current = tocEntries.get(i);
						int start = 0;
						int end = 0;

						start = current.startPage;
						end = current.endPage;

						// 5) 새 PDF 생성
						try (PDDocument sectionDoc = new PDDocument()) {
							for (int p = start; p <= end; p++) {
								// 1. 원본 페이지 가져오기
								PDPage originalPage = document.getPage(p - 1);

								// 2. 원본 페이지를 import (리소스를 sectionDoc 안으로 deep copy)
								PDPage importedPage = sectionDoc.importPage(originalPage);

								// 3. 주석 제거
								importedPage.setAnnotations(Collections.emptyList());
							}

							// 4. AcroForm 제거
							PDAcroForm form = sectionDoc.getDocumentCatalog().getAcroForm();
							if (form != null) {
								form.setFields(Collections.emptyList());
								form.setXFA(null);
							}

							// 6) 저장 (예: "02_가나다_순_특약_색인.pdf")
							String safeTitle = Normalizer.normalize(current.title, Normalizer.Form.NFKC) // 유니코드 정규화
									.replaceAll("[\\x00-\\x1F]", "")				// 모든 제어 문자 제거 (\u0000 포함)
									.replaceAll("[\\u00A0\\u3000]", " ")			 // 특수 공백 → 일반 공백
									.replaceAll("[\\\\/:*?\"<>|]", "")			   // 파일명 불가 문자 제거
									.replaceAll("\\s+", "_")						 // 공백 → 언더바
									.replaceAll("_+", "_")
									.replaceAll("^_+|_+$", "")
									.trim();
							String fileName = String.format("%03d_%s.pdf", i, safeTitle);
							File outFile = new File(outputDir, fileName);

							// 디렉토리 존재 여부 확인
							if (!outputDir.exists()) {
								boolean created = outputDir.mkdirs();
								if (!created) {
									throw new IOException("❌ 출력 디렉토리 생성 실패: " + outputDir.getAbsolutePath());
								}
							}

							sectionDoc.save(outFile);

							File file = outFile;
							if (!file.exists()) {
								log.info("파일 없음: " + outputDir + File.separator + current.number + "_" + safeTitle + ".pdf");
								continue;
							}

							try (FileInputStream fis = new FileInputStream(file)) {
								byte[] buffer = new byte[1024];

								zos.putNextEntry(new ZipEntry(file.getName()));

								int length;
								while ((length = fis.read(buffer)) > 0) {
									zos.write(buffer, 0, length);
								}

								zos.closeEntry();
							}
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
					throw new CmmnBizException("pdf 자동분할 처리 작업 중 오류가 발생했습니다.", e);
				}
			} catch (IOException e) {
				throw new CmmnBizException("pdf 자동분할 처리 작업 중 오류가 발생했습니다.", e);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 출력
		return ResponseEntity.ok()
				.headers(headers)
				.body(resource);

	}

	public ResponseEntity<Resource> mergePC(@Valid PdfMergeDto dto) {

		List<String> fileNmList = dto.getFileNmList();
		String saveFileNm = new StringBuilder()
				.append(dto.getSaveFileNm())
				.append(".pdf")
				.toString();
		String taskDir = dto.getTaskDir();

		// 합본대상 pdf 전체경로 목록작성
		List<String> fileFullpathList = new ArrayList<String>();
		for(String fileNm : fileNmList) {
			String fileFullPath = new StringBuilder()
					.append(cmmnProperties.getFileMng().getUploadRootPath())
					.append(File.separator)
					.append(taskDir)
					.append(File.separator)
					.append(fileNm)
					.toString();
			fileFullpathList.add(fileFullPath);
		}

		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 합본pdf를 저장할 파일포함 full 경로
		String mergedFileTmpPath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
				.append(createTmpFolder())  // 임시작업폴더 생성한다.
				.append(File.separator)
				.append(UuidUtils.getUuidOnlyString())
				.append(".pdf")
				.toString();


		PDFMergerUtility merger = new PDFMergerUtility();
		try {
			for(String path : fileFullpathList) {
				merger.addSource(new File(path));
			}
			// 출력 파일 경로 설정
			merger.setDestinationFileName(mergedFileTmpPath);
			// 병합 실행
			merger.mergeDocuments(null); // MemoryUsageSetting.defaultMemoryUsage() 대신 null도 가능


			try( PDDocument document = Loader.loadPDF(new File(mergedFileTmpPath))) {

				// PDF 리소스 최적화 실행
				PDFResourceOptimizer optimizer = new PDFResourceOptimizer();
				optimizer.optimizeResources(document);
				optimizer.printOptimizationStats();


				String final_file_path = mergedFileTmpPath + "_02";

				// 병합 후 불필요한 리소스 제거
				for (PDPage page : document.getPages()) {
					page.setAnnotations(Collections.emptyList()); // 주석 제거
				}

				// 최적화된 문서 저장
				document.save(final_file_path);

				// 리소스 준비
				File mergedFile = new File(final_file_path);

				InputStreamResource resource = new InputStreamResource(new FileInputStream(mergedFile));

				// 한글 파일명 인코딩
				String encodedFileName = URLEncoder.encode(saveFileNm, StandardCharsets.UTF_8.toString())
												   .replaceAll("\\+", "%20");
				// HTTP 헤더 설정
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_PDF);
				headers.setContentDispositionFormData("attachment", encodedFileName);

				// ResponseEntity 생성
				return ResponseEntity.ok()
					.headers(headers)
					.body(resource);

			}
		} catch (IOException e) {
			throw new CmmnBizException("pdf 파일합본작업중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * pdf 미리보기값 추출
	 * @param taskDir
	 * @param tmpFileNm
	 * @return
	 */
	public String previewTmp(String taskDir, String tmpFileNm) {

		String fullFilePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
				.append(taskDir)
				.append(File.separator)
				.append(tmpFileNm)
				.toString();

		File pdfFile = new File(fullFilePath);

		try (PDDocument document = Loader.loadPDF(pdfFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage image = pdfRenderer.renderImageWithDPI(0, 200); // 200 DPI로 렌더링

			ImageIO.write(image, "png", baos);

			byte[] imageBytes = baos.toByteArray();
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch(IOException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
			return "";
		}
	}

	public String preview(String fileNo, int sort) {
		FileInfoDto fileInfo =  commonFileService.getFileInfo(fileNo);

		String fullFilePath = new StringBuilder()
				.append(fileInfo.getFilePath())
				.append(File.separator)
				.append(fileNo)
				.toString();

		File pdfFile = new File(fullFilePath);

		try (PDDocument document = Loader.loadPDF(pdfFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			BufferedImage image = pdfRenderer.renderImageWithDPI(sort, 200); // 200 DPI로 렌더링

			ImageIO.write(image, "png", baos);

			byte[] imageBytes = baos.toByteArray();
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch(IOException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
			return "";
		}
	}

	public PageList<FileInfoDto> getListPage(SearchPrdClusPdfDto dto, PageConfig pageConfig) {
		String memId = RequestUtils.getUser().getMemId();
		String comCode = cmmnProperties.getComCode();

		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());

		String[] escapeStrList = new String[] {"%", "_"};

		dto.setFileNm(CoreUtils.appendEscape(dto.getFileNm(), escapeStrList, dto.getDbDriverId()));

		dto.setMemId(memId);
		dto.setComCode(comCode);
		PageList<FileInfoDto> list = appDao.selectListPage("api.cnt.prdtcluspdfmng.getList_TB_CM_FILE", dto, pageConfig);
		return list;
	}

	public List<FileInfoDto> getList(SearchPrdClusPdfDto dto) {
		String memId = RequestUtils.getUser().getMemId();
		String comCode = cmmnProperties.getComCode();

		dto.setDbDriverId(cmmnProperties.getAppDatabaseId());
		dto.setMemId(memId);
		dto.setComCode(comCode);
		return appDao.selectList("api.cnt.prdtcluspdfmng.getList_TB_CM_FILE", dto);
	}

	public PdfInfoDto getPdfInfo(String fileNo) {

		PdfInfoDto rslt = PdfInfoDto.builder()
				.fileNo(fileNo)
				.build();

		FileInfoDto fileInfoDto = commonFileService.getFileInfo(fileNo);
		String fullFolderPath = new StringBuilder()
				.append(fileInfoDto.getFilePath())
				.append(File.separator)
				.append(fileNo)
				.toString();

		File pdfFile = new File(fullFolderPath);
		// 파일이 존재하지 않으면
		if(!pdfFile.exists()) {
			return rslt;
		}

		List<PdfPreviewDto> previewList = new ArrayList<PdfPreviewDto>();
		try (PDDocument document = Loader.loadPDF(pdfFile)) {

			int size = document.getNumberOfPages();
			for(int i=0; i<size; i++) {

				PDFRenderer pdfRenderer = new PDFRenderer(document);
				BufferedImage image = pdfRenderer.renderImageWithDPI(i, 20);
				try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){

					ImageIO.write(image, "png", baos);

					byte[] imageBytes = baos.toByteArray();

					String previewBase64 = Base64.getEncoder().encodeToString(imageBytes);
//					log.debug(">>> previewBase64: {}", previewBase64);

					PdfPreviewDto pdfPreviewDto = PdfPreviewDto.builder()
							.sort(i)
							.previewBase64(previewBase64)
							.build();
					previewList.add(pdfPreviewDto);
				}
			}
		} catch(IOException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
			return rslt;
		}
		rslt.setPreviewList(previewList);

		return rslt;
	}

	public void deleteList(List<FileInfoDto> dtoList) {
		dtoList.forEach(dto -> delete(dto));
	}


	public List<Map<String, Object>> deleteListPrevChk(List<FileInfoDto> dtoList) {
		return dtoList
				.stream()
				.map(dto -> {
					Map<String, Object> rslt = new HashMap<String, Object>();

					String fileNo = dto.getFileNo();
					int count = appDao.selectOne("api.cnt.prdtcluspdfmng.chkExist_TB_PRDT_INFO_CHG_LST", fileNo);
					rslt.put("fileNm", dto.getFileNm());
					rslt.put("crtDtmChar", dto.getCrtDtmChar());
					rslt.put("count", count);

					return rslt;
				})
				.collect(Collectors.toList())
		;
	}

	public void delete(FileInfoDto dto) {
		String fileNo = dto.getFileNo();
		commonFileService.delete(fileNo);
	}

	/**
	 * pdf약관 신규생성처리
	 * @param pdfBlob
	 * @param saveFileNm
	 */
	public void addNew(MultipartFile pdfBlob, String saveFileNm) {

		String fileNm = new StringBuilder()
				.append(saveFileNm)
				.append(".pdf")
				.toString();

		FileInfoDto fileInfoDto = commonFileService.uploadFile(
					null,
					null,
					pdfBlob,
					cmmnProperties.getPdfDir(),
					fileNm,
					"CLUS"
				);

		// 상품별 파일정보 확인 및 업데이트
		setClusFileInfo(fileInfoDto);
	}

	/**
	 * pdf 약관 업로드 처리
	 * @param pdfFiles
	 * @return
	 */
	public List<FileInfoDto> upPdf(List<MultipartFile> pdfFiles) {

		List<FileInfoDto> rslt = new ArrayList<FileInfoDto>();

		for(MultipartFile pdfFile : pdfFiles) {
			FileInfoDto fileInfo = commonFileService.uploadFile(
						null,
						null,
						pdfFile,
						cmmnProperties.getPdfDir(),
						pdfFile.getOriginalFilename(),
						"CLUS"
					);
			rslt.add(fileInfo);

			// 상품별 파일정보 확인 및 업데이트
			setClusFileInfo(fileInfo);
		}
		return rslt;
	}

	/**
	 * 상품별 파일정보 확인 및 업데이트
	 * @param fileInfo
	 */
	public void setClusFileInfo(FileInfoDto fileInfo) {

		// 파일명이 상품의 상품코드이자 약관파일명
		String fileNm = fileInfo.getFileNm();
		String prdtCd = fileNm.substring(0, fileNm.lastIndexOf("."));

		// fileNm 와 상품코드가 일치하는 상품이 존재하는지 확인
		PrdInfoDto prdInfoDto = prdtMngService.getPrdtInfo(prdtCd);
		if(prdInfoDto == null) {
			return;
		}

		// 만약 해당상품의 fileNm 이 존재하지 않거나, 상품코드와 일치하면 fileNo 를 업데이트한다.
		if(StringUtils.isEmpty(prdInfoDto.getFileNm()) || prdtCd.equals(prdInfoDto.getFileNm())) {
			PrdInfoDto params = PrdInfoDto.builder()
					.prdtCd(prdtCd)
					.fileNo(fileInfo.getFileNo())
					.prdtChgYmd(prdInfoDto.getPrdtChgYmd())
					.build();

			// 상품이력의 파일아이디 업데이트
			appDao.update("api.cnt.prdtcluspdfmng.updatePrdtFileNo_TB_PRDT_INFO_CHG_LST", params);
			// 약관구성항목의 파일아이디 업데이트
			appDao.update("api.cnt.prdtcluspdfmng.updatePrdtFileNo_TB_CLUS_ITM_SET_MST", params);
		}
	}

	/**
	 * 개별약관생성이력 로그를 저장한다.
	 * @param mxtrClusCd
	 * @param prdtClusList_All
	 */
	private void writeIndvClusRcvLog(String mxtrClusCd, List<PrdtClusDto> prdtClusList_CT) {


		String[] mxtrClusCdAry = mxtrClusCd.split("-");

		// 주계약코드를 추출한다.
		String prdtCd = mxtrClusCdAry[0];
		// 특약조합코드(16진수조합)를 추출한다.
		String mxtrCd = mxtrClusCdAry[1];
		// 계약일자를 추출한다.
		String contYmd = mxtrClusCdAry[2];

		// 특약조합코드(2진수조합)를 추출한다.
		StringBuilder sb_binaryResult = new StringBuilder();
		for (char ch : mxtrCd.toCharArray()) {
			// 각 문자 (16진수) → BigInteger
			BigInteger decimal = new BigInteger(String.valueOf(ch), 16);
			// 2진수 문자열로 변환하고 4자리로 맞춤
			String binary = String.format("%4s", decimal.toString(2)).replace(' ', '0');
			sb_binaryResult.append(binary);  // 붙이기
		}
		// 최종 이진수 문자열 → 배열로 분리
		String[] binaryArry = sb_binaryResult.toString().split("");


		// 주계약을 추출한다.
		PrdtClusDto prdt = prdtClusList_CT.stream().filter(p -> "C".equals(p.getClusItmClcd())).findAny().orElseThrow(() -> new CmmnBizException("주계약이 존재하지 않습니다."));


		// 개별약관조합 DB 테이블에 입력할 해당 데이터의 index를 생성한다.
		String index = CoreUtils.genTimestampUniqId();

//		StringBuffer requestURL = request.getRequestURL(); // 스킴 + 호스트 + 포트 + 경로
//		String queryString = request.getQueryString();	 // 쿼리 파라미터
//		String sndurl = StringUtils.isEmpty(queryString)
//						? requestURL.toString()
//						: requestURL.append('?').append(queryString).toString();

		//발송URL은 이전 페이지 유입경로로 변경을 요청하여 수정
		String sndurl = request.getHeader("Referer") != null ? request.getHeader("Referer") : strSndurl;

		String memId = RequestUtils.getUser().getMemId();
		// 이력로그용 Dto 작성
		IndvClusRcvMstDto indvClusRcvMstDto = IndvClusRcvMstDto.builder()
				.indvClusMxtrId(index)  // 개별약관조합ID
				.mxtrClusCd(					 // 조합약관코드
						new StringBuilder()
						.append(prdtCd)
						.append('-')
						.append(mxtrCd)
						.toString()
				)
				.cmpnyCd(prdt.getCmpnyCode())  // 회사코드
				.sndurl(sndurl)				// 발송URL
				.contYmd(contYmd)			  // 계약일자
				.crtr(memId)
				.updusr(memId)
				.dbDriverId(cmmnProperties.getAppDatabaseId())
				.build();

		appDao.insert("api.cnt.prdtcluspdfmng.insertLog_TB_INDV_CLUS_RCV_MST", indvClusRcvMstDto);

		int size = prdtClusList_CT.size();
		for(int i=0; i<size; i++) {
			PrdtClusDto prdtClus = prdtClusList_CT.get(i);

			String sbscrbYn = "1".equals(binaryArry[i]) ? "Y" : "N";

			IndvClusRcvDtlDto indvClusRcvDtlDto = IndvClusRcvDtlDto.builder()
					.indvClusMxtrId(index)  // 개별약관조합ID
					.clusItmCd(prdtClus.getClusItmCd())  // 약관항목코드
					.clusItmNm(prdtClus.getClusItmNm())  // 약관항목명
					.sbscrbYn(sbscrbYn)  // 가입여부
					.crtr(memId)
					.updusr(memId)
					.build();

			appDao.insert("api.cnt.prdtcluspdfmng.insertLog_TB_INDV_CLUS_RCV_DTL", indvClusRcvDtlDto);
		}
	}


	/**
	 * 최종출력대상 목록 생성
	 * @param mxtrClusCd
	 * @return
	 */
	private CmmnMap crtPrdtClusList(
			String mxtrClusCd // 조합약관코드. 예) L001C0001-FF80C-20250426
			) {

		String[] mxtrClusCdAry = mxtrClusCd.split("-");

		// 주계약코드를 추출한다.
		String prdtCd = mxtrClusCdAry[0];
		// 특약조합코드(16진수조합)를 추출한다.
		String mxtrCd = mxtrClusCdAry[1];
		// 계약일자를 추출한다.
		String contYmd = mxtrClusCdAry[2];

		// 특약조합코드(2진수조합)를 추출한다.
		StringBuilder sb_binaryResult = new StringBuilder();
		for (char ch : mxtrCd.toCharArray()) {
			BigInteger decimal = new BigInteger(String.valueOf(ch), 16);  // 16진 → BigInteger
			String binary = String.format("%4s", decimal.toString(2)).replace(' ', '0');  // 2진 문자열, 4비트 맞춤
			sb_binaryResult.append(binary);
		}
		String binaryResult = sb_binaryResult.toString();  // 예시) 10100110011110

		// 페이징하단 높이
		List<Float> pageFldList = new ArrayList<Float>();
		pageFldList.add((float) 35);

		// 주계약의 전체 셋팅된 목록을 가지고 온다.
		List<PrdtClusDto> prdtClusList_All = prdtClusMngService.getInfo(prdtCd);
		// 약관파일정보를 계약일자기준 최근정보로 변경한다.
		prdtClusList_All
		.stream()
		.filter(item -> StringUtils.isNotEmpty(item.getFileNo()))
		.forEach(item -> {
			// 약관항목코드 추춣
			String clusItmCd = item.getClusItmCd();

			// 표지의 경우 페이징값를 가지고와서 재셋팅한다.
			if("M".equals(item.getClusItmClcd())) {
				pageFldList.add(0, item.getPageFld());
			}

			// 약관항목별 이력을 추출
			List<PrdInfoChgLstDto> prdInfoChgLst = prdtMngService.getPrdInfoChgLst(clusItmCd);
			// 상품변경일자가 계약일자기준보다 작은대상으로 추출
			prdInfoChgLst = prdInfoChgLst.stream()
				.filter(a -> {
					return   StringUtils.isNotEmpty(a.getFileNo()) // 파일정보가 존재하고,
							&& (Integer.parseInt(a.getPrdtChgYmd()) <= Integer.parseInt(contYmd)) // 상품변경일자가 계약일자이전
							;
				})
				.collect(Collectors.toList())
				;
			//파일정보가 있으면 20250708 yyg
			if(prdInfoChgLst.size() > 0) {
				// 상품변경일자로 내림차순으로 정렬
				prdInfoChgLst.sort((a, b) -> Integer.parseInt(b.getPrdtChgYmd()) - Integer.parseInt(a.getPrdtChgYmd()));

				// 상품변경일자기준으로 가장 최근정보
				PrdInfoChgLstDto target = prdInfoChgLst.get(0);
				item.setFileNm(target.getFileNm());
				item.setFileNo(target.getFileNo());
			}
		});


		// 주계약을 추출한다.
		PrdtClusDto prdt = prdtClusList_All.stream().filter(p -> "C".equals(p.getClusItmClcd())).findAny().orElseThrow(() -> new CmmnBizException("주계약이 존재하지 않습니다."));

		// screnDispOrd 로 정렬한다.
		prdtClusList_All.sort((a, b) -> a.getScrenDispOrd() - b.getScrenDispOrd());

		// 주계약,특약만 추출한다.
		List<PrdtClusDto> prdtClusList_CT =
				prdtClusList_All.stream()
					.filter(p -> "C".equals(p.getClusItmClcd()) || "T".equals(p.getClusItmClcd()) )
					.collect(Collectors.toList());

		log.debug(">>> mxtrClusCd: {}", mxtrClusCd);
		log.debug(">>> binaryResult: {}", binaryResult);
		log.debug(">>> prdtClusList_All: {}", JsonUtils.toJsonStrPretty(prdtClusList_All));

		int size;
		// binaryResult 값을 통해서 적용대상 주계약,특약만 추출한다.
		List<PrdtClusDto> prdtClusList_CT_Y = new ArrayList<PrdtClusDto>();
		String[] binaryResultAry = binaryResult.split("");
		size = prdtClusList_CT.size();
		for(int i = 0; i < size; i++) {
			if("1".equals(binaryResultAry[i])) {
				prdtClusList_CT_Y.add(prdtClusList_CT.get(i));
			}
		}

		// 최종출력대상 목록을 셋팅한다.
		List<PrdtClusDto> prdtClusList = new ArrayList<PrdtClusDto>();
		prdtClusList_All.forEach(p -> {
			String clusItmClcd = p.getClusItmClcd();
			if(!"T".equals(clusItmClcd)) {
				// 특약이 아닌경우는 무조건 추가한다.
				prdtClusList.add(p);
			} else {
				// 특약일 경우는 prdtClusList_CT 에 존재하는 약관만 추가한다.
				String clusItmCd = p.getClusItmCd();
				if( prdtClusList_CT_Y.stream().anyMatch(a -> a.getClusItmCd().equals(clusItmCd)) ) {
					prdtClusList.add(p);
				}
			}
		});
		log.debug(">>> prdtClusList: {}", JsonUtils.toJsonStrPretty(prdtClusList));

		// 개별약관생성이력 로그를 저장한다.
		writeIndvClusRcvLog(mxtrClusCd, prdtClusList_CT);

		return new CmmnMap()
				.put("prdtClusList", prdtClusList)
				.put("prdt", prdt)
				.put("pageFld", pageFldList.get(0))
				;
	}


	public String createQrCode(String mxtrClusCd, String isView) {
		try {
			// QR 정보
			int width = 100; // 가로 pixel
			int height = 100; // 세로 pixel

			StringBuilder strUrl = new StringBuilder("");
			String url = "";

			if("Y".equals(isView)) {
				width = 200; // 가로 pixel
				height = 200; // 세로 pixel
				url = strUrl.append(mxtrClusCd)
							.toString();
			} else {
				url = strUrl.append(cmmnProperties.getQrUrl())
							.append("?mxtrClusCd=")
							.append(mxtrClusCd)
							.append("&sndurl=qrCode")
							.toString();
			}

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
			return Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
		} catch (WriterException | IOException e) {
			return null;
		}
	}



	/**
	 * 약관조합코드에 의한 개별약관 생성
	 * @param mxtrClusCd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResponseEntity<Resource> indvClusRcv(
				String mxtrClusCd, // 조합약관코드. 예) L001C0001-FF80C-20250426
				String sndurl, // 유입경로 Url req.getHeader("Referer")가 null인 경우가 있기 때문이다.
				String isMergeAddHistYn // 약관내보내기이력 저장여부. 예) "Y" 또는 "N"
			) {

		strSndurl = sndurl;

		CmmnMap rslt = crtPrdtClusList(mxtrClusCd);

		// 최종출력대상 목록 생성
		List<PrdtClusDto> prdtClusList = (List<PrdtClusDto>) rslt.get("prdtClusList");

		// 주계약 추출
		PrdtClusDto prdt = (PrdtClusDto) rslt.get("prdt");

		// 페이징하단 높이
		float pageFld = rslt.getFloat("pageFld");


		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 임시작업폴더 생성한다.
		String taskDir = createTmpFolder();

		// 합본pdf를 저장할 파일포함 full 경로
		String fullFilePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
				.append(taskDir)
				.append(File.separator)
				.append(UuidUtils.getUuidOnlyString())
				.append(".pdf")
				.toString();

		PDFMergerUtility merger = new PDFMergerUtility();
		try {

			// 표지에 들어갈 QR 코드이미지를 생성한다.
			String qrImage = createQrCode(mxtrClusCd, "N");

			// 목차에 들어갈 내용을 셋팅준비한다.
			PdfTocDto pdfTocDto = PdfTocDto.builder()
					.title("목차")
					.build();

			List<TocContent> tocContentList = new ArrayList<PdfTocDto.TocContent>();

			// 최종출력대상 목록을 대상으로 합본작업을 진행한다.
			int pageCnt = 1;
			for(PrdtClusDto clus : prdtClusList) {
				String filePath = getFullFilePath(clus.getFileNo());
				if(StringUtils.isEmpty(filePath)) {

					continue;
				}

				// pdf 파일을 추출한다.
				File pdfFile = new File(filePath);

				// pdf 파일이 존재할 경우
				if(pdfFile.exists()) {
					// 표지일 경우
					if("M".equals(clus.getClusItmClcd())) {


						byte[] decodedBytes = Base64.getDecoder().decode(qrImage);

						try(PDDocument document = Loader.loadPDF(new File(filePath));
								ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
								) {

							String tempMFilePath = new StringBuilder()
									.append(cmmnProperties.getFileMng().getUploadRootPath())
									.append(File.separator)
									.append(taskDir)
									.append(File.separator)
									.append(UuidUtils.getUuidOnlyString())
									.append(".pdf")
									.toString();
							pdfFile = new File(tempMFilePath);
							clus.setTmpFilePath(tempMFilePath);

							PDPage page = document.getPage(0);

							BufferedImage image = ImageIO.read(bis);

							PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);

							// 페이지 크기 계산
							float qrWidth = (float) (100 / 1.33);   // QR 너비 (point) 100 pixel 대응
							float qrHeight = (float) (100 / 1.33);  // QR 높이 (point) 100 pixel 대응
							float margin = 10;	// 여백

							float pageWidth = page.getMediaBox().getWidth();
//							float pageHeight = page.getMediaBox().getHeight();

							float x = pageWidth - qrWidth - margin;
							// pageFld 단위는 point
							float y = margin + pageFld;

							// QR 이미지 삽입
							try (PDPageContentStream contentStream
									= new PDPageContentStream(
											document,
											page,
											PDPageContentStream.AppendMode.APPEND,
											true
											)
									) {
								contentStream.drawImage(
										pdImage,
										x,
										y,
										qrWidth,  // qr 이미지 가로사이즈
										qrHeight  // qr 이미지 세로사이즈
										);
							}
							// 저장
							document.save(tempMFilePath, new CompressParameters(2000));
							filePath = tempMFilePath;
						}
					}
					pdfFile = new File(filePath);

					// 목차에 들어갈 내용을 셋팅한다.
					// 목차에 들어갈 내용은 주계약과 특약만으로 한다.
					if("C".equals(clus.getClusItmClcd()) || "T".equals(clus.getClusItmClcd())) {
						TocContent tocContent = TocContent.builder()
								.title(CoreUtils.normalizeNFC(clus.getClusItmNm()))  //
								.cls(clus.getClusItmClcd())
								.page(Integer.toString(pageCnt))
								.build();
						tocContentList.add(tocContent);
					}
					int pageNum = Loader.loadPDF(pdfFile).getNumberOfPages();
					pageCnt += pageNum;
				}
			}
			pdfTocDto.setTocContentList(tocContentList);

			 // 목차파일을 생성한다.
			File fill_P = null;
			PrdtClusDto clus_P = prdtClusList.stream()
					.filter(clus -> "P".equals(clus.getClusItmClcd()))  // 목차일 경우
					.findAny()
					.orElse(null);
			if(clus_P != null) {
				fill_P = createTOCPage(
							getFullFilePath(clus_P.getFileNo()),  // 대상 PDF파일
							pdfTocDto  // 목차정보
						);
			}

			// 합본처리를 한다.
			for(PrdtClusDto clus : prdtClusList) {
				String filePath = clus.getTmpFilePath();
				if(StringUtils.isEmpty(filePath)) {
					filePath = getFullFilePath(clus.getFileNo());
				}
				if(StringUtils.isEmpty(filePath)) {
					continue;
				}
				File file = new File(filePath);

				// pdf 파일이 존재할 경우
				if(file.exists()) {
					// 목차일 경우
					if("P".equals(clus.getClusItmClcd())) {
						merger.addSource(fill_P);
					} else {
						merger.addSource(new File(filePath));
					}
				}
			}

			// 출력 파일 경로 설정
			merger.setDestinationFileName(fullFilePath);
			// 병합 실행
			merger.mergeDocuments(null); // MemoryUsageSetting.defaultMemoryUsage() 대신 null도 가능

			// 주계약의 이름을 다운로드파일이름으로 한다.
			String saveFileName = prdt.getClusItmNm() + ".pdf";
			// 출력용 PDF 파일생성
			File rsltFile = new File(fullFilePath);
			if(!rsltFile.exists()) {
				throw new CmmnBizException("PDF 파일생성중 오류가 발생했습니다.");
			}


			// 출력용 PDF 파일에 페이지 셋팅
			String file_path_setpage = setPdfPage(
					fullFilePath, // 작업대상 PDF 파일
					pageFld  // 페이지를 표시하기 위한 pdf 하단에서의 높이
				);


			try(PDDocument document = Loader.loadPDF(new File(file_path_setpage))){

				// PDF 리소스 최적화 실행
				PDFResourceOptimizer optimizer = new PDFResourceOptimizer();
				optimizer.optimizeResources(document);
				optimizer.printOptimizationStats();

				String final_path = file_path_setpage + "_02";

				// 최적화된 문서 저장
				document.save(final_path);

				Resource resource = new FileSystemResource(new File(final_path));

				// 한글명 인코딩
				String encodedFileName = URLEncoder
									 		.encode(saveFileName, StandardCharsets.UTF_8.toString())
									 		.replaceAll("\\+", "%20"); // 공백 처리

				// 출력 헤더셋팅
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_PDF);
				headers.setContentDispositionFormData("attachment", encodedFileName);
				headers.setContentLength(resource.contentLength());


				String indvClusMergeaddId = CoreUtils.genTimestampUniqId();

				String memId = RequestUtils.getUser().getMemId();

				IndvClusMergeaddInfDto indvClusMergeaddInfDto = IndvClusMergeaddInfDto.builder()
						.indvClusMergeaddId(indvClusMergeaddId)
						.indvClusMxtrId(mxtrClusCd)
						.clusNm(prdt.getClusItmNm())
						.fileAtchDir("")
						.emalSndYn("N")
						.emalAdr("")
						.crtr(memId)
						.updusr(memId)
						.build();

				if("Y".equals(isMergeAddHistYn)) {
					appDao.insert("api.cnt.prdtcluspdfmng.insertInfo_TB_INDV_CLUS_MERGEADD_INF", indvClusMergeaddInfDto);
				}

//				try (PDDocument doc4Textload = Loader.loadPDF(new File(fullFilePath))) {
//
//					// 1. Create PDFTextStripper instance
//					PDFTextStripper textStripper = new PDFTextStripper();
//
//					// 2. Configure extraction settings (optional)
//					textStripper.setSortByPosition(true); // Maintains physical layout
//					textStripper.setStartPage(1); // First page to extract
//					textStripper.setEndPage(doc4Textload.getNumberOfPages()); // Last page
//
//					// 3. Extract text from entire document
//					String text = textStripper.getText(doc4Textload);
//
//					indvClusMergeaddInfDto.setTxtText(text);
//
//					if("Y".equals(isMergeAddHistYn)) {
//						appDao.insert("api.cnt.prdtcluspdfmng.insertInfo_TB_INDV_CLUS_MERGEADD_INF", indvClusMergeaddInfDto);
//					}
//
//					appDao.insert("api.cnt.prdtcluspdfmng.insertInfo_TB_INDV_CLUS_MERGE_INF", indvClusMergeaddInfDto);
//
//				}

				// 출력
				return ResponseEntity.ok()
						.headers(headers)
						.body(resource);
			}
		} catch (IOException e) {
			throw new CmmnBizException("pdf 파일합본작업중 오류가 발생했습니다.", e);
		}

	}

	private String getFullFilePath(String fileNo) {
		FileInfoDto fileInfo = commonFileService.getFileInfo(fileNo);
		if(fileInfo == null) {
			return null;
		}
		return new StringBuilder()
				.append(fileInfo.getFilePath())
				.append(File.separator)
				.append(fileInfo.getFileNo())
				.toString();
	}

	@SuppressWarnings("unchecked")
	public ResponseEntity<Resource> indvClusRcvSplitDn(String mxtrClusCd) {

		CmmnMap rslt = crtPrdtClusList(mxtrClusCd);

		// 최종출력대상 목록 생성
		List<PrdtClusDto> prdtClusList = (List<PrdtClusDto>) rslt.get("prdtClusList");

		// 주계약 추출
		PrdtClusDto prdt = (PrdtClusDto) rslt.get("prdt");

		// 2시간이전의 임시폴더는 모두 삭제한다.
		deleteOldTmpFolder();

		// 임시작업폴더 생성한다.
		String taskDir = createTmpFolder();

		// 압축처리할 파일경로
		String zipFilePath = new StringBuilder()
				.append(cmmnProperties.getFileMng().getUploadRootPath())
				.append(File.separator)
				.append(taskDir)
				.append(File.separator)
				.append(UuidUtils.getUuidOnlyString())
				.toString();

		// 압축처리할 파일
		File zipFile = new File(zipFilePath);
		try(FileOutputStream fos = new FileOutputStream(zipFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ZipOutputStream zos = new ZipOutputStream(bos)){

			zos.setLevel(Deflater.BEST_COMPRESSION);

			int cnt = 0;
			// 압축파일 추가처리를 한다.
			for(PrdtClusDto clus : prdtClusList) {

				String filePath = getFullFilePath(clus.getFileNo());
				if(StringUtils.isEmpty(filePath)) {
					continue;
				}
				File itemFile = new File(filePath);

				try (FileInputStream fis = new FileInputStream(itemFile)) {
					cnt++;
					String clusItmNm = clus.getClusItmNm().replaceAll("/", ",").replaceAll("\\+", "%20"); // 공백 처리

					String itemFileNm = new StringBuilder()
							.append(StringUtils.leftPad(Integer.toString(cnt), 3, "0"))
							.append("_")
							.append(clusItmNm)
							.append(".pdf")
							.toString();

					ZipEntry zipEntry = new ZipEntry(itemFileNm);
					zos.putNextEntry(zipEntry);

					byte[] buffer = new byte[1024];
					int length;
					while ((length = fis.read(buffer)) != -1) {
						zos.write(buffer, 0, length);
					}
					zos.closeEntry();
				}
			}
			Resource resource = new FileSystemResource(zipFile);

			// 한글명 인코딩
			String encodedFileName = URLEncoder
								 		.encode(prdt.getClusItmNm() + ".zip", StandardCharsets.UTF_8.toString())
								 		.replaceAll("\\+", "%20"); // 공백 처리

			// 출력 헤더셋팅
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.valueOf("application/zip"));
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", encodedFileName);


			String memId = RequestUtils.getUser().getMemId();

			String indvClusMergeaddId = CoreUtils.genTimestampUniqId();

			IndvClusMergeaddInfDto indvClusMergeaddInfDto = IndvClusMergeaddInfDto.builder()
					.indvClusMergeaddId(indvClusMergeaddId)
					.indvClusMxtrId(mxtrClusCd)
					.clusNm(prdt.getClusItmNm())
					.fileAtchDir("")
					.emalSndYn("N")
					.emalAdr("")
					.crtr(memId)
					.updusr(memId)
					.build();

			appDao.insert("api.cnt.prdtcluspdfmng.insertInfo_TB_INDV_CLUS_MERGEADD_INF", indvClusMergeaddInfDto);

			// 출력
			return ResponseEntity.ok()
					.headers(headers)
					.body(resource);
		} catch (IOException e) {
			throw new CmmnBizException("PDF 분리압축작업중 오류가 발생했습니다.", e);
		}

	}

}
