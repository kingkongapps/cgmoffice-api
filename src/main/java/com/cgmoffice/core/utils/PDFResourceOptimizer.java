package com.cgmoffice.core.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDMMType1Font;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.SymbolEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.ZapfDingbatsEncoding;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PDFResourceOptimizer {

    // 중복 체크를 위한 해시 맵들
    private Map<String, PDFont> uniqueFonts = new HashMap<>();
    private Map<String, PDImageXObject> uniqueImages = new HashMap<>();
    private Map<String, PDFormXObject> uniqueForms = new HashMap<>();

    /**
     * PDF 문서의 리소스를 최적화합니다.
     */
    public void optimizeResources(PDDocument document) throws IOException {

        PDDocumentCatalog catalog = document.getDocumentCatalog();
        PDPageTree pages = catalog.getPages();

        // 1단계: 모든 리소스 수집 및 중복 체크
        collectUniqueResources(pages);

        // 2단계: 각 페이지의 리소스 최적화
        for (PDPage page : pages) {
            optimizePageResources(page);
        }

        log.debug("최적화 완료:");
        log.debug("고유 폰트 수: " + uniqueFonts.size());
        log.debug("고유 이미지 수: " + uniqueImages.size());
        log.debug("고유 폼 수: " + uniqueForms.size());
    }

    /**
     * 모든 페이지에서 고유한 리소스들을 수집합니다.
     */
    private void collectUniqueResources(PDPageTree pages) throws IOException {
        for (PDPage page : pages) {
            PDResources resources = page.getResources();
            if (resources != null) {
                collectFonts(resources);
                collectImages(resources);
                collectForms(resources);
            }
        }
    }

    /**
     * 폰트 리소스 수집 및 중복 체크
     */
    private void collectFonts(PDResources resources) throws IOException {
        for (COSName fontName : resources.getFontNames()) {
            PDFont font = resources.getFont(fontName);
            if (font != null) {
                String fontKey = generateFontKey(font);

                // 중복 체크: 이미 존재하지 않는 경우에만 추가
                if (!uniqueFonts.containsKey(fontKey)) {
                    uniqueFonts.put(fontKey, font);
                    log.debug("새 폰트 추가: " + fontKey);
                } else {
                	log.debug("중복 폰트 발견: " + fontKey);
                }
            }
        }
    }

    /**
     * 이미지 리소스 수집 및 중복 체크
     */
    private void collectImages(PDResources resources) throws IOException {
        for (COSName imageName : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(imageName);
            if (xObject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xObject;
                String imageKey = generateImageKey(image);

                if (!uniqueImages.containsKey(imageKey)) {
                    uniqueImages.put(imageKey, image);
                    log.debug("새 이미지 추가: " + imageKey +
                                     " (크기: " + image.getWidth() + "x" + image.getHeight() + ")");
                } else {
                	log.debug("중복 이미지 발견: " + imageKey);
                }
            }
        }
    }

    /**
     * 폼 XObject 리소스 수집 및 중복 체크
     */
    private void collectForms(PDResources resources) throws IOException {
        for (COSName formName : resources.getXObjectNames()) {
            PDXObject xObject = resources.getXObject(formName);
            if (xObject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xObject;
                String formKey = generateFormKey(form);

                if (!uniqueForms.containsKey(formKey)) {
                    uniqueForms.put(formKey, form);
                    log.debug("새 폼 추가: " + formKey);

                    // 폼 내부의 리소스도 재귀적으로 처리
                    PDResources formResources = form.getResources();
                    if (formResources != null) {
                        collectFonts(formResources);
                        collectImages(formResources);
                        collectForms(formResources);
                    }
                } else {
                	log.debug("중복 폼 발견: " + formKey);
                }
            }
        }
    }

    /**
     * 개별 페이지의 리소스를 최적화된 리소스로 교체
     */
    private void optimizePageResources(PDPage page) throws IOException {
        PDResources resources = page.getResources();
        if (resources == null) return;

        PDResources newResources = new PDResources();

        // 폰트 리소스 최적화
        optimizeFontResources(resources, newResources);

        // 이미지 리소스 최적화
        optimizeImageResources(resources, newResources);

        // 폼 리소스 최적화
        optimizeFormResources(resources, newResources);

        // 기타 리소스 복사 (ColorSpace, Pattern, Shading 등)
        copyOtherResources(resources, newResources);

        page.setAnnotations(Collections.emptyList()); // 주석 제거

        // 최적화된 리소스로 교체
        page.setResources(newResources);
    }

    /**
     * 폰트 리소스 최적화
     */
    private void optimizeFontResources(PDResources oldResources, PDResources newResources)
            throws IOException {
        for (COSName fontName : oldResources.getFontNames()) {
            PDFont font = oldResources.getFont(fontName);
            if (font != null) {
                String fontKey = generateFontKey(font);
                PDFont optimizedFont = uniqueFonts.get(fontKey);
                if (optimizedFont != null) {
                    newResources.put(fontName, optimizedFont);
                }
            }
        }
    }

    /**
     * 이미지 리소스 최적화
     */
    private void optimizeImageResources(PDResources oldResources, PDResources newResources)
            throws IOException {
        for (COSName imageName : oldResources.getXObjectNames()) {
            PDXObject xObject = oldResources.getXObject(imageName);
            if (xObject instanceof PDImageXObject) {
                PDImageXObject image = (PDImageXObject) xObject;
                String imageKey = generateImageKey(image);
                PDImageXObject optimizedImage = uniqueImages.get(imageKey);
                if (optimizedImage != null) {
                    newResources.put(imageName, optimizedImage);
                }
            }
        }
    }

    /**
     * 폼 리소스 최적화
     */
    private void optimizeFormResources(PDResources oldResources, PDResources newResources)
            throws IOException {
        for (COSName formName : oldResources.getXObjectNames()) {
            PDXObject xObject = oldResources.getXObject(formName);
            if (xObject instanceof PDFormXObject) {
                PDFormXObject form = (PDFormXObject) xObject;
                String formKey = generateFormKey(form);
                PDFormXObject optimizedForm = uniqueForms.get(formKey);
                if (optimizedForm != null) {
                    newResources.put(formName, optimizedForm);
                }
            }
        }
    }

    /**
     * 기타 리소스들 복사 (ColorSpace, Pattern, Shading 등)
     */
    private void copyOtherResources(PDResources oldResources, PDResources newResources) {
    	try {
    		// ColorSpace 복사
            for (COSName name : oldResources.getColorSpaceNames()) {
                newResources.put(name, oldResources.getColorSpace(name));
            }
            // Pattern 복사
            for (COSName name : oldResources.getPatternNames()) {
                newResources.put(name, oldResources.getPattern(name));
            }
            // Shading 복사
            for (COSName name : oldResources.getShadingNames()) {
                newResources.put(name, oldResources.getShading(name));
            }
            // ExtGState 복사
            for (COSName name : oldResources.getExtGStateNames()) {
                newResources.put(name, oldResources.getExtGState(name));
            }
            // Properties 복사
            for (COSName name : oldResources.getPropertiesNames()) {
                newResources.put(name, oldResources.getProperties(name));
            }
        } catch (IOException e) {
            log.error("기타 리소스 복사 실패", e);
        }
    }


	/**
	 * 폰트의 고유 키 생성 (더 정밀한 타입 정보 포함)
	 */
	private String generateFontKey(PDFont font) {
	    StringBuilder key = new StringBuilder("font_");

	    // 1. 기본 클래스 정보
	    key.append(font.getClass().getSimpleName());

	    // 2. 더 정밀한 폰트 타입 정보
	    String preciseType = getPreciseFontType(font);
	    key.append("_precise_").append(preciseType);

	    // 3. 기존 정보들
	    key.append("_name_").append(safe(font.getName()));
	    key.append("_subtype_").append(safe(font.getSubType()));
	    key.append("_type_").append(safe(font.getType()));
	    key.append("_embedded_").append(font.isEmbedded());

	    // 4. 추가 식별 정보
	    key.append("_hasToUnicode_").append(font.toUnicode(65) != null);
	    key.append("_encoding_").append(safe(getEncodingInfo(font)));

	    key.append("_hash_").append(getFontDataHash(font));

	    return key.toString();
	}

	/**
	 * 정밀한 폰트 타입 분석
	 */
	private String getPreciseFontType(PDFont font) {
	    StringBuilder typeInfo = new StringBuilder();

	    // 1. 폰트 딕셔너리에서 직접 타입 정보 추출
	    COSDictionary fontDict = font.getCOSObject();
	    if (fontDict != null) {
	        COSName type = (COSName) fontDict.getDictionaryObject(COSName.TYPE);
	        COSName subtype = (COSName) fontDict.getDictionaryObject(COSName.SUBTYPE);

	        typeInfo.append("dict_type_").append(safe(type != null ? type.getName() : "null"));
	        typeInfo.append("_dict_subtype_").append(safe(subtype != null ? subtype.getName() : "null"));
	    }

	    // 2. 구체적인 폰트 클래스별 세부 정보
	    if (font instanceof PDType0Font) {
	        PDType0Font type0Font = (PDType0Font) font;
	        typeInfo.append("_composite");

	        // CIDFont 정보
	        try {
	            PDCIDFont cidFont = type0Font.getDescendantFont();
	            if (cidFont != null) {
	                typeInfo.append("_cid_").append(safe(cidFont.getClass().getSimpleName()));

	                COSDictionary cidDict = cidFont.getCOSObject();
	                COSName subType = (COSName) cidDict.getDictionaryObject(COSName.SUBTYPE);
	                String subTypeName = subType != null ? subType.getName() : "unknown";

	                typeInfo.append("_cid_subtype_").append(subTypeName);

	                // CIDSystemInfo
	                PDCIDSystemInfo sysInfo = cidFont.getCIDSystemInfo();
	                if (sysInfo != null) {
	                    typeInfo.append("_registry_").append(safe(sysInfo.getRegistry()));
	                    typeInfo.append("_ordering_").append(safe(sysInfo.getOrdering()));
	                    typeInfo.append("_supplement_").append(sysInfo.getSupplement());
	                }
	            }
	        } catch (Exception e) {
	            typeInfo.append("_cid_error");
	        }

	    } else if (font instanceof PDTrueTypeFont) {
	        typeInfo.append("_truetype");

	        // TrueType 특정 정보
	        try {
	            TrueTypeFont ttf = ((PDTrueTypeFont) font).getTrueTypeFont();
	            if (ttf != null) {
	                typeInfo.append("_ttf_version_").append(ttf.getVersion());
	                typeInfo.append("_num_glyphs_").append(ttf.getNumberOfGlyphs());
	            }
	        } catch (IOException e) {
	            typeInfo.append("_ttf_error");
	        }

	    } else if (font instanceof PDType1Font) {
	        typeInfo.append("_type1");

	        // Type1 특정 정보
	        PDType1Font type1Font = (PDType1Font) font;
	        typeInfo.append("_standard_").append(type1Font.isStandard14());

	    } else if (font instanceof PDType3Font) {
	        typeInfo.append("_type3");

	        // Type3 특정 정보 (사용자 정의 폰트)
	        PDType3Font type3Font = (PDType3Font) font;
	        try {
	            BoundingBox bbox = type3Font.getBoundingBox();
	            if (bbox != null) {
	                typeInfo.append("_bbox_").append((int)bbox.getWidth()).append("x").append((int)bbox.getHeight());
	            }
	        } catch (Exception e) {
	            typeInfo.append("_bbox_error");
	        }

	    } else if (font instanceof PDType1CFont) {
	        typeInfo.append("_type1c_cff");

	    } else if (font instanceof PDMMType1Font) {
	        typeInfo.append("_mmtype1");
	    }

	    // 3. 폰트 서술자 정보
	    try {
	        PDFontDescriptor descriptor = font.getFontDescriptor();
	        if (descriptor != null) {
	            typeInfo.append("_flags_").append(descriptor.getFlags());

	            // 폰트 파일 타입 확인
	            if (descriptor.getFontFile() != null) {
	                typeInfo.append("_file_type1");
	            } else if (descriptor.getFontFile2() != null) {
	                typeInfo.append("_file_truetype");
	            } else if (descriptor.getFontFile3() != null) {
	                // Type1C 또는 CIDFontType0C
	                COSDictionary file3Dict = descriptor.getFontFile3().getCOSObject();
	                COSName subtype3 = (COSName) file3Dict.getDictionaryObject(COSName.SUBTYPE);
	                typeInfo.append("_file3_").append(safe(subtype3 != null ? subtype3.getName() : "unknown"));
	            }
	        }
	    } catch (Exception e) {
	        typeInfo.append("_descriptor_error");
	    }

	    return typeInfo.toString();
	}

	/**
	 * 인코딩 정보 추출
	 */
	private String getEncodingInfo(PDFont font) {
	    try {
	        StringBuilder encodingInfo = new StringBuilder();

	        // 기본 인코딩 정보
	        if (font instanceof PDSimpleFont) {
	            PDSimpleFont simpleFont = (PDSimpleFont) font;
	            Encoding encoding = simpleFont.getEncoding();
	            if (encoding != null) {
	                encodingInfo.append(encoding.getClass().getSimpleName());

	                // 구체적인 인코딩 타입
	                if (encoding instanceof WinAnsiEncoding) {
	                    encodingInfo.append("_WinAnsi");
	                } else if (encoding instanceof MacRomanEncoding) {
	                    encodingInfo.append("_MacRoman");
	                } else if (encoding instanceof StandardEncoding) {
	                    encodingInfo.append("_Standard");
	                } else if (encoding instanceof SymbolEncoding) {
	                    encodingInfo.append("_Symbol");
	                } else if (encoding instanceof ZapfDingbatsEncoding) {
	                    encodingInfo.append("_ZapfDingbats");
	                } else if (encoding instanceof DictionaryEncoding) {
	                    encodingInfo.append("_Dictionary");
	                }
	            }
	        } else if (font instanceof PDType0Font) {
	            // CMap 정보
	            PDType0Font type0Font = (PDType0Font) font;
	            CMap cmap = type0Font.getCMap();
	            if (cmap != null) {
	                encodingInfo.append("CMap_").append(safe(cmap.getName()));
	                encodingInfo.append("_registry_").append(safe(cmap.getRegistry()));
	                encodingInfo.append("_ordering_").append(safe(cmap.getOrdering()));
	            }
	        }

	        return encodingInfo.length() > 0 ? encodingInfo.toString() : "no_encoding";

	    } catch (Exception e) {
	        return "encoding_error";
	    }
	}

	/**
	 * 폰트 해시 생성 (바이트 레벨 비교용)
	 */
	private String getFontDataHash(PDFont font) {
		try {
			PDFontDescriptor descriptor = font.getFontDescriptor();
			if (descriptor != null) {
				// 실제 폰트 데이터의 해시값 계산
				PDStream fontFile = null;
				if (descriptor.getFontFile3() != null) {
					fontFile = descriptor.getFontFile3();
				} else if (descriptor.getFontFile2() != null) {
					fontFile = descriptor.getFontFile2();
				} else if (descriptor.getFontFile() != null) {
					fontFile = descriptor.getFontFile();
				}

				if (fontFile != null) {
					try (InputStream is = fontFile.createInputStream()) {
						byte[] buffer = new byte[1024];
						int hash = 1;
						int bytesRead;
						while ((bytesRead = is.read(buffer)) != -1) {
							for (int i = 0; i < bytesRead; i++) {
								hash = 31 * hash + buffer[i];
							}
						}
						return String.valueOf(Math.abs(hash));
					}
				}
			}
			return "no_data";
		} catch (IOException e) {
			return "hash_error";
		}
	}

	private String safe(String str) {
		return (str == null) ? "null" : str.replaceAll("[^a-zA-Z0-9]", "_");
	}

    /**
     * 이미지의 고유 키 생성 (크기, 색상 공간, 비트 깊이 기반)
     */
    private String generateImageKey(PDImageXObject image) throws IOException {
        StringBuilder key = new StringBuilder();
        key.append("image_");
        key.append(image.getWidth()).append("x").append(image.getHeight());
        key.append("_").append(image.getBitsPerComponent());

        if (image.getColorSpace() != null) {
            key.append("_").append(image.getColorSpace().getName().replaceAll("[^a-zA-Z0-9]", "_"));
        }

        // 압축 필터 정보
        COSDictionary dict = image.getCOSObject();
        if (dict.containsKey(COSName.FILTER)) {
            key.append("_").append(dict.getItem(COSName.FILTER).toString().replaceAll("[^a-zA-Z0-9]", "_"));
        }

        key.append("_").append(getImageDataHash(image));

        return key.toString();
    }

    private String getImageDataHash(PDImageXObject image) {
        try {
            // 이미지의 원본 스트림 데이터를 사용하여 해시 계산
            COSStream cosStream = image.getCOSObject();
            if (cosStream != null) {
                try (InputStream is = cosStream.createInputStream()) {
                    byte[] buffer = new byte[1024];
                    int hash = 1;
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        for (int i = 0; i < bytesRead; i++) {
                            hash = 31 * hash + buffer[i];
                        }
                    }
                    return String.valueOf(Math.abs(hash));
                }
            }

            // COSStream이 null인 경우 BufferedImage를 통한 해시 계산
            BufferedImage bufferedImage = image.getImage();
            if (bufferedImage != null) {
                // 이미지의 픽셀 데이터를 기반으로 해시 계산
                int hash = 1;
                hash = 31 * hash + bufferedImage.getWidth();
                hash = 31 * hash + bufferedImage.getHeight();
                hash = 31 * hash + bufferedImage.getType();

                // 샘플링을 통한 픽셀 데이터 해시 (성능 최적화)
                int step = Math.max(1, Math.min(bufferedImage.getWidth(), bufferedImage.getHeight()) / 100);
                for (int y = 0; y < bufferedImage.getHeight(); y += step) {
                    for (int x = 0; x < bufferedImage.getWidth(); x += step) {
                        hash = 31 * hash + bufferedImage.getRGB(x, y);
                    }
                }

                return String.valueOf(Math.abs(hash));
            }

            return "no_data";
        } catch (IOException e) {
            return "hash_error";
        } catch (Exception e) {
            return "processing_error";
        }
    }

    /**
     * 폼의 고유 키 생성 (BBox, Matrix 기반)
     */
    private String generateFormKey(PDFormXObject form) throws IOException {
        StringBuilder key = new StringBuilder();
        key.append("form_");

        // BoundingBox 정보
        if (form.getBBox() != null) {
            key.append("bbox_").append(form.getBBox().toString().replaceAll("[^a-zA-Z0-9]", "_"));
        }

        // Matrix 정보
        if (form.getMatrix() != null) {
            key.append("_matrix_").append(form.getMatrix().toString().replaceAll("[^a-zA-Z0-9]", "_"));
        }

        // 스트림 길이 (내용의 유사성 체크)
        COSStream stream = form.getCOSObject();
        if (stream != null) {
            key.append("_length_").append(stream.getLength());
        }

        return key.toString();
    }

    /**
     * 리소스 최적화 통계 출력
     */
    public void printOptimizationStats() {
    	log.debug("\n=== 리소스 최적화 통계 ===");
    	log.debug("고유 폰트 수: " + uniqueFonts.size());
    	log.debug("고유 이미지 수: " + uniqueImages.size());
    	log.debug("고유 폼 수: " + uniqueForms.size());

    	log.debug("\n폰트 목록:");
        uniqueFonts.keySet().forEach(key -> log.debug("  - " + key));

        log.debug("\n이미지 목록:");
        uniqueImages.keySet().forEach(key -> log.debug("  - " + key));

        if (!uniqueForms.isEmpty()) {
        	log.debug("\n폼 목록:");
            uniqueForms.keySet().forEach(key -> log.debug("  - " + key));
        }
    }
}