package com.cgmoffice.core.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Throwables;
import com.cgmoffice.core.constant.BrowserCode;
import com.cgmoffice.core.constant.CoreConstants;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.jwt.TokenProvider;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CoreUtils {

	// 특정날자의 요일 구하기
	public String getWeekNm(int year, int month, int day) {
		DayOfWeek dayOfWeek = LocalDate.of(year , month, day).getDayOfWeek();
		return dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN);
	}

	/**
	 * response의 헤더중 Content-Disposition 지정하기
	 * @param filename
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void setContectDispositionResponse(String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String dispositionPrefix = "attachment; filename=";
		String encodedFilename = encodeString4Browser(request, filename);
		response.setHeader("Content-Disposition", dispositionPrefix + encodedFilename);
	}

	/**
	 * 예외에서 상세내용을 stirng 형식으로 리턴
	 * @param ex
	 * @return
	 */
	public String getExceptionStackTrace(Exception ex) {
		return Throwables.getStackTraceAsString(ex);
	}

	/**
	 * bean명으로 bean을 가져오는 함수
	 * @param beanId
	 * @return
	 */
	public Object getBean(String beanId) {

		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
		if(applicationContext == null) {
			throw new CmmnBizException("Spring의 Application이 초기화가 안됨");
		}
		return applicationContext.getBean(beanId);
	}

	/**
	 * 브라우저 종류 파악하기
	 * @param request
	 * @return
	 */
	public BrowserCode getBrowser(HttpServletRequest request) {

		String header = request.getHeader(HttpHeaders.USER_AGENT).toUpperCase();
		if(header.contains("MSIE") || header.contains("TRIDENT")) {
			return BrowserCode.MSIE;
		} else if(header.contains("EDGE")) {
			return BrowserCode.EDGE;
		} else if(header.contains("CHROME")) {
			return BrowserCode.CHROME;
		} else if(header.contains("OPERA")) {
			return BrowserCode.OPERA;
		} else if(header.contains("SAFARI")) {
			return BrowserCode.SAFARI;
		}
		return BrowserCode.FIREFOX;
	}

	/**
	 * 호출한 브라우져용 string 변환
	 * @param request
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public String encodeString4Browser(HttpServletRequest request, String str) {
		BrowserCode browser = getBrowser(request);

		String rslt = null;
		if(BrowserCode.MSIE.equals(browser) || BrowserCode.EDGE.equals(browser)) {
			try {
				rslt = URLEncoder.encode(str, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
			} catch (UnsupportedEncodingException e) {
				rslt = "";
			}
		} else {
			rslt = new String(str.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		}
		return rslt;
	}

	/**
	 * 숫자 콤마찍기
	 * @param num
	 * @return
	 */
	public String numberCommas(Object obj) {

		String num;
		if(obj instanceof Integer) {
			num = Integer.toString((int)obj);
		} else if(obj instanceof Long) {
			num = Long.toString((long)obj);
		} else if(obj instanceof Double) {
			num = Double.toString((double)obj);
		} else {
			num = (String) obj;
		}

		Double inValues = Double.parseDouble(num);
		DecimalFormat Commas = new DecimalFormat("#,###");
		String result_int = (String) Commas.format(inValues);
		return result_int;
	}

	/**
	 * 현재 컴퓨터의 mac주소를 하이픈 포함해서 가져옴(17자리)
	 * <br> 주의 : java 1.6 이상부터 사용이 가능함
	 * <br> 예시 : E8-03-9A-68-D2-7F
	 * @return 하이픈(-)이 포함된 17자리 mac주소
	 */
	public String getServerMacHyphen(){
		// 기본값 mac주소를 세팅
		String macAddr = "";

		// 실제 mac주소 세팅
		try {
			// byte형식의 mac주소
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();

			// byte코드를 String 자료형의 16진수로 변환
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < mac.length; i++){
				sb.append(String.format("%02X%s", mac[i], (i < mac.length -1 ) ? "-" : ""));
			}

			macAddr = sb.toString();
		} catch (IOException e) {
			// 오류가 발생하면 mac주소를 기본값으로 세팅함
			macAddr = "";
		}

		return macAddr.replace("-", "");
	}

	/**
	 * 클라이언트 아이피 주소 획득
	 * @param request
	 * @return
	 */
	public String getIpAddr(HttpServletRequest request) {

		String ip = request.getHeader("X-Forwarded-For");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

	/**
	 * java class 의 함수를 실행
	 * @param objClass 실행할 java class
	 * @param strCallMethod실행할 함수명
	 * @param lParam 실행할 함수에 입력되는 parameter 목록
	 * @return
	 */
	public Object callObjectMethodParamObj(Class objClass, String strCallMethod, List lParam) {

		try {

			int nParam = 0;

			if(lParam == null) {
				nParam = 0;
			} else {
				nParam = lParam.size();
			}

			Class paramClass[] = new Class[nParam];
			Object objParam[] = new Object[nParam];

			for(int i=0; i<nParam; i++) {
				paramClass[i] = lParam.get(i).getClass();
				objParam[i] = lParam.get(i);
			}

			Method objMethod = objClass.getMethod(strCallMethod, paramClass);

			if(objMethod.getReturnType() == void.class) {
				objMethod.invoke(objClass.getDeclaredConstructor().newInstance(), objParam);
				return void.class;
			} else {
				return objMethod.invoke(objClass.getDeclaredConstructor().newInstance(), objParam);
			}
		} catch (Exception e) {
			return new CmmnBizException("callObjectMethodParamObj error!");
		}
	}

	/**
	 * process id 를 추출해주는 함수
	 * @return
	 */
	public String getPid() {
		String pid;
		pid = ManagementFactory.getRuntimeMXBean().getName();
		pid = pid.substring(0, pid.indexOf('@'));
		return pid;
	}

	/**
	 * mac os의 경우 한글 자모가 분리되는 것을 보정해주는 함수
	 * @param src
	 * @return
	 */
	public String normalizeNFC(String src) {
		if(StringUtils.isEmpty(src)) return src;
		// 1. NFC 정규화
	    String normalized = Normalizer.normalize(src, Normalizer.Form.NFC);
	    // 2. UTF-8 바이트 배열로 변환
	    byte[] utf8Bytes = normalized.getBytes(StandardCharsets.UTF_8);
	    // 3. 만약 문자열로 다시 바꿔야 한다면
	    return new String(utf8Bytes, StandardCharsets.UTF_8);
	}

	/**
	 * MultipartFile 에서 확장자를 추출하는 함수
	 * @param multipartFile
	 * @return
	 */
	public String getExt(MultipartFile multipartFile) {
		String orgFileNm = multipartFile.getOriginalFilename(); // 원시파일명
		return orgFileNm.substring(orgFileNm.lastIndexOf(".") + 1); // 파일확장자
	}

	/**
	 * 터미널의 명령어를 실행시키는 함수
	 * @param command
	 */
	public void shellCmd(String command) {
		log.info(">>> 명령어 : " + command);
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(command);
		} catch (IOException e1) {
			log.error(CoreUtils.getExceptionStackTrace(e1));
			return;
		}
		try(InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);){

	        String line;
	        while((line = br.readLine()) != null) {
               log.info(line);
	        }
		} catch (IOException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
		} finally {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				log.error(CoreUtils.getExceptionStackTrace(e));
			}
			process.destroy();
		}
	}

	/**
	 * get 방식으로 호출된 request 의 query 값을 CmmnMap 형식으로 변환해주는 함수
	 *
	 * @param request
	 * @return
	 */
	public CmmnMap requestQuery2CmmnMap(HttpServletRequest request) {

		CmmnMap rslt = new CmmnMap();
    	try {
    		String qstr = request.getQueryString();
    		if(StringUtils.isNotEmpty(qstr)) {
        		String uri = StringUtils.join(request.getRequestURI(), "?", qstr);
        	    MultiValueMap<String, String> parameters =
        	            UriComponentsBuilder.fromUriString(uri).build().getQueryParams();

        	    Set<String> keys = parameters.keySet();

        	    String value;
        	    for(String key : keys) {
        	    	value = parameters.get(key).get(0);
        	    	if(StringUtils.isEmpty(value)) {
            			rslt.put(key, "");
        	    	} else {
            			rslt.put(key, CoreStringUtils.encodeXSS(   URLDecoder.decode(value, CoreConstants.GLOBAL_CHARSET.name()) )     );
        	    	}
        	    }
    		}
		} catch (UnsupportedEncodingException e) {
			log.error(CoreUtils.getExceptionStackTrace(e));
		}

		return rslt;
	}

	/**
	 * map 형식을 VO 형식으로 변환해주는 함수
	 * @param <T>
	 * @param map
	 * @param type
	 * @return
	 */
	public <T> T convertToVO(Map<String, Object> map, Class<T> type) {
		if(type == null) {
			throw new NullPointerException("Class cannot be null");
		}

		try {
			T instance = type.getConstructor().newInstance();
			if(map == null || map.isEmpty()) {
				return instance;
			}

			for(Map.Entry<String, Object> entrySet : map.entrySet()) {
				Field[] fields = type.getDeclaredFields();
				for(Field field : fields) {
					field.setAccessible(true);
					String fieldName = field.getName();
					boolean isSametype = entrySet.getValue().getClass().equals(field.getType());
					boolean isSameName = entrySet.getKey().equals(fieldName);
					if(isSametype && isSameName) {
						field.set(instance, map.get(fieldName));
					}
				}
			}

			return instance;
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * VO 오브젝트로부터 필드명에 해당하는 필드의 값을 추출해내는 함수
	 * @param fieldName
	 * @param vo
	 * @return
	 */
	public Object getValueFromVoByFiledName(String fieldName, Object vo) {

		for(Method method : vo.getClass().getMethods()) {
			String methodName = method.getName();
			if((methodName.startsWith("get") && methodName.length() == fieldName.length() + 3)
					|| (methodName.startsWith("is") && methodName.length() == fieldName.length() + 2)) {
				if(methodName.toLowerCase().endsWith(fieldName.toLowerCase())) {
					try {
						return method.invoke(vo);
					} catch (Exception e) {
						return null;
					}
				}
			}
		}
		return null;
	}

	/**
	 * request 의 cookie 에서 cookieName 에 해당하는 값을 리턴하는 함수
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public String getCookieValue(HttpServletRequest request, String cookieName) {

		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return null;
		}

	    return Arrays.stream(cookies)
	            .filter(c -> c.getName().equals(cookieName))
	            .findFirst()
	            .map(Cookie::getValue)
	            .orElse(null);
	}

	/**
	 * oracle 의 clob 을 string 으로 변환해주는 함수
	 * @param clob
	 * @return
	 */
	public String clobToString(Clob clob) {
		if(clob == null) return "";
		StringBuilder sb = new StringBuilder();
	    char []chars = new char[512];
	    try(Reader in = clob.getCharacterStream()) {
	        while (in.read(chars) >= 0) {
	            sb.append(chars);
	            chars = new char[512];
	        }
	    }
	    catch (Exception e) {
	    	log.error(getExceptionStackTrace(e));
	    }

	    return sb.toString().replaceAll("\\u0000", "");
	}

	/**
	 * close 가 가능한 모든 객체를 안전하게 닫아주는 함수
	 * @param closeables
	 */
	public void safeClose(Closeable... closeables) {
		if(closeables == null) {
			return;
		}
		for(Closeable closeable : closeables) {
			if(closeable == null) {
				continue;
			}
			try {
				closeable.close();
			} catch(IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * 만나이 구하기 함수
	 * @param birthYear
	 * @param birthMonth
	 * @param birthDay
	 * @return
	 */
	public int getManAge(int birthYear, int birthMonth, int birthDay) {

		Calendar current = Calendar.getInstance();

		int currentYear = current.get(Calendar.YEAR);
		int currentMonth = current.get(Calendar.MONTH) + 1;
		int currentDay = current.get(Calendar.DAY_OF_MONTH);

		// 만 나이 구하기 2022-1955=27 (현재년-태어난년)
		int age = currentYear - birthYear;
		if(birthMonth * 100 + birthDay > currentMonth * 100 + currentDay) {
			age--;
		}
		// 5월 26일 생은 526
		// 현재날짜 5원 25일은 525
		// 두수를 비교했을때 생일이 더 클경우 생일이 지나지 않은 것이다.

		return age;

	}


	/**
	 * 파일이 이미지형식인지 확인하는 함수
	 * @param f
	 * @return
	 */
	public boolean isImageFile(File f) {
		String mimeType = null;

		try {
			mimeType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(new FileInputStream(f)));
		} catch (IOException var3) {
			log.error(var3.getMessage());
			return false;
		}

		return StringUtils.startsWithIgnoreCase(mimeType, "image/");
	}

	/**
	 * 이미지파일을 base64 코드로 변환하는 함수
	 * @param file
	 * @return
	 */
	public String convertImg2Base64(File file) {

		try(FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				) {


			int len = 0;
			byte[] buf = new byte[1024];

			while((len=fis.read(buf)) != -1) {
				bao.write(buf, 0, len);;
			}

			byte[] fileArry = bao.toByteArray();

			return new String(Base64.encodeBase64(fileArry));

		}catch (IOException e) {
			log.debug(CoreUtils.getExceptionStackTrace(e));
			return CoreConstants.NOIMAGE2;
		}
	}

	/**
	 *
	 * @param imgfile 변환할 이미지파일
	 * @param maxWidth 변환시 최대넓이값
	 * @return
	 */
	public String convertImg2Base64ReSize(File imgfile, int maxWidth) {

		try(FileInputStream fis = new FileInputStream(imgfile);
				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				) {

	        BufferedImage inputImage = ImageIO.read(fis);  // 받은 이미지 읽기

	        int originWidth = inputImage.getWidth();
	        int originHeight = inputImage.getHeight();

	        int newWidth = originWidth;
	        int newHeight = originHeight;
	        if(originWidth > maxWidth) {
	        	newWidth = maxWidth;
	        	newHeight = (originHeight * newWidth) / originWidth;
	        }
	        /*축소 이미지 생성*/
	        int type = inputImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : inputImage.getType();
	        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
	        Graphics2D g = resizedImage.createGraphics();
	        g.drawImage(inputImage, 0, 0, newWidth, newHeight, null);
	        g.dispose();

	        ImageIO.write(resizedImage, "png", bao);

			byte[] fileArry = bao.toByteArray();

			return new String(Base64.encodeBase64(fileArry));

		}catch (IOException e) {
			log.debug(CoreUtils.getExceptionStackTrace(e));
			return CoreConstants.NOIMAGE2;
		}
	}



	/* 리사이즈 실행 메소드 */
    public BufferedImage resize(InputStream inputStream, int width, int height)
    		throws IOException {

        BufferedImage inputImage = ImageIO.read(inputStream);  // 받은 이미지 읽기

        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());
        // 입력받은 리사이즈 길이와 높이

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null); // 그리기
        graphics2D.dispose(); // 자원해제



        return outputImage;
    }

    public void trimDtoFields(Object obj, Class objClass) {
    	for (Field field : objClass.getDeclaredFields()) {
            if (field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(obj);
                    if (" ".equals(value)) {
                        field.set(obj, "");
                    } else if (value != null && value.trim().isEmpty()) {
                        field.set(obj, "");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	public <T> T cast(Class<T> type, Object obj) {
		return JsonUtils.fromJsonStr(type, JsonUtils.toJsonStr(obj));
	}

	public <T> T cast(TypeReference<T> typeRef, Object obj) {
		return JsonUtils.fromJsonStr(typeRef, JsonUtils.toJsonStr(obj));
	}

	public Map<String, String> getHeaders(HttpServletResponse response) {
		Map<String, String> headers = new HashMap<>();
		response.getHeaderNames().forEach( a -> {
			headers.put(a, response.getHeader(a));
		});
		return headers;
	}

	public CmmnMap getParameters(HttpServletRequest request){
		CmmnMap parameters = new CmmnMap();
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()) {
			String paramName = params.nextElement();
			String paramValue = request.getParameter(paramName);
			parameters.put(paramName, paramValue);
		}
		return parameters;
	}

	public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
		StringBuilder respMessage = new StringBuilder();
		Map<String, String> headers = getHeaders(response);
		respMessage.append("RESPONSE ");
		respMessage.append("\t method = [").append(request.getMethod()).append("]");
		if(!headers.isEmpty()) {
			respMessage.append(" ResponseHeaders = [").append(headers).append("]");
		}
		respMessage.append(" responseBody = [").append(body).append("]");
		log.info("$$###>Log Response: {}", respMessage);
	}

	public void displayReq(HttpServletRequest request, Object body) {
		StringBuilder reqMessage = new StringBuilder();
		CmmnMap parameters = getParameters(request);

		reqMessage.append("REQUEST ");
		reqMessage.append("method = [").append(request.getMethod()).append("]");
		reqMessage.append(" path = [").append(request.getRequestURI()).append("]");

		if(!"POST".equalsIgnoreCase(request.getMethod()) && !parameters.isEmpty()) {
			reqMessage.append("\n Request parameters = [").append(parameters).append("]");
		}
		if(!Objects.isNull(body)) {
			reqMessage.append("\n Request body = [").append(body).append("]");
		}
		log.info("$$###>Log Request: {}", reqMessage);
	}

	/**
	 * 모든 HTML 태그를 제거하고 반환한다.
	 *
	 * @param html
	 * @throws Exception
	 */
	public String removeTag(String html) {
		return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	public String replaceToHtmlName(String str) {
		return str
//				.replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;")
				.replaceAll(" ", "&nbsp;")
				;
	}

	public String replaceFromHtmlName(String str) {
		if(str == null) return "";
		return str
//				.replaceAll("&amp;", "&")
				.replaceAll("&lt;", "<")
				.replaceAll("&gt;", ">")
				.replaceAll("&nbsp;", " ")
				;
	}

	public String replaceNewLine(String str, String target) {
		return str
//				.replaceAll("&amp;", "&")
				.replaceAll("\\r\\n", target)
				.replaceAll("\\r", target)
				.replaceAll("\\n", target)
				;
	}

	public String convertATagGotoExWeb(String conts) {
		Pattern pattern = Pattern.compile("<a[^>]*href=(\\\"([^\\\"]*)\\\"|\\'([^\\']*)\\'|([^\\\\s>]*))[^>]*>(.*?)</a>");
		Matcher matcher = pattern.matcher(conts);
		while(matcher.find()) {
			String href_ori = matcher.group(1);
			String href = href_ori.substring(1, href_ori.length()-1);

			String oriStr = new StringBuilder()
					.append("href=")
					.append(href_ori)
					.toString();
			String toStr = new StringBuilder()
					.append("href=\"#none\" onclick=\"CMMN.gotoExWeb('")
					.append(href)
					.append("')\" ")
					.toString();

			conts = new StringBuilder()
				.append(conts.substring(0, conts.indexOf(oriStr)))
				.append(toStr)
				.append(conts.substring(conts.indexOf(oriStr) + oriStr.length(), conts.length()))
				.toString();
		}
		return conts;
	}

	public String convertHttpStringGotoExWeb(String conts) {
        String regex = "\\b(?:https?):\\/\\/[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";

        conts = conts.replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>");

        Matcher matcher = Pattern.compile(regex).matcher(conts);

        int offset = 0;

        List<String> strList = new ArrayList<String>();
        while (matcher.find()) {

        	int start = matcher.start();
        	int end = matcher.end();

        	strList.add(conts.substring(offset, start));
        	offset = start;
        	strList.add(conts.substring(offset, end));
        	offset = end;
        }
        strList.add(conts.substring(offset));


        if(strList.isEmpty()) return conts;

        StringBuilder sb = new StringBuilder();

        strList.stream().forEach(a -> {
        	if(a.startsWith("http")) {
        		a = new StringBuilder()
        				.append("<a href=\"#none\" onclick=\"CMMN.gotoExWeb('")
        				.append(a)
    					.append("')\"> ")
        				.append(a)
        				.append("</a>")
        				.toString();
        	}
    		sb.append(a);
        });
        return sb.toString();
	}

	public String convertTelnoString(String conts) {
        String regex = "(\\+\\d{1,3}[\\s-]?)?(\\(\\d{1,3}\\)[\\s-]?)?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{1,4}";

        conts = conts.replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>");

        Matcher matcher = Pattern.compile(regex).matcher(conts);

        int offset = 0;

        List<String> strList = new ArrayList<String>();
        while (matcher.find()) {

        	int start = matcher.start();
        	int end = matcher.end();

        	strList.add(conts.substring(offset, start));
        	offset = start;
        	strList.add(conts.substring(offset, end));
        	offset = end;
        }
        strList.add(conts.substring(offset));


        if(strList.isEmpty()) return conts;

        StringBuilder sb = new StringBuilder();

        strList.stream().forEach(a -> {
        	if(a.startsWith("010")) {
        		a = new StringBuilder()
        				.append("<a href=\"tel:")
        				.append(a.replaceAll("-", ""))
    					.append("\">")
        				.append(a)
        				.append("</a>")
        				.toString();
        	}
    		sb.append(a);
        });
        return sb.toString();
	}

	/**
	 * 특정폴더를 통째로 삭제처리
	 * @param targetFolder
	 * @return
	 */
	public boolean deleteDirectoryAndFiles(File targetFolder) {
        if(!targetFolder.exists()) {
            log.error("{} >>> 경로가 존재하지 않습니다.", targetFolder);
            return false;
        }

        File[] files = targetFolder.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                log.debug("{} >>> 파일은 디렉토리입니다. 하위 파일을 확인하겠습니다.", file);
                deleteDirectoryAndFiles(file);
            }
            file.delete();
            log.debug("{} >>> 파일이 삭제되었습니다.", file);
        }

        return targetFolder.delete();
    }

	// jwt 토큰을 추출한다.
	public String resolveToken(HttpServletRequest request, HttpServletResponse response) {
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith(TokenProvider.AUTH_STR_BEARER)) {
			String jwt = bearerToken.substring(7);
			String[] jwtAry = jwt.split(",");
			log.debug(">>> 헤더의 jwt: {}", jwtAry[0]);
			return jwtAry[0];
		} else {
			// 쿠키에서도 토큰존재여부를 확인을 한다.
			if (request.getCookies() != null) {
		        for (Cookie cookie : request.getCookies()) {
		            if ("jwt".equals(cookie.getName())) {
		            	String jwt = cookie.getValue();
		            	if(StringUtils.isNotEmpty(jwt)) {
		            		log.debug(">>> 쿠키의 jwt: {}", jwt);
			                return jwt; // JWT 토큰 반환
		            	}
		            }
		        }
		    }
		}
		return null;
	}

	public String genTimestampUniqId() {
		return new StringBuilder()
				.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")))
				.append(1000 + (new Random()).nextInt(9000))  // 1000 ~ 9999 랜덤한숫자
				.toString();
	}

	public String getRequestBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();

        try(InputStream inputStream
        		= request.getInputStream()) {

            if (inputStream != null) {
            	try(BufferedReader bufferedReader
            			= new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){

	                char[] charBuffer = new char[128];
	                int bytesRead = -1;
	                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                    stringBuilder.append(charBuffer, 0, bytesRead);
	                }
            	}
            } else {
                stringBuilder.append("");
            }
        }

        body = stringBuilder.toString();

        //body = URLDecoder.decode(body, ShinhanContext.GLOBAL_CHARSET.name());
        return body;
    }

	public byte[] readAllBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int nRead;
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		return buffer.toByteArray();
	}

	/**
	 * 특수 문자 일부 기호에 한하여, ESCAPE 문자를 붙여준다.
	 * @param String str, String[] escapeStrList
	 * @return String
	 */
	public String appendEscape(String str, String[] escapeStrList, String dbDriverId) {
		StringBuffer newStr = new StringBuffer("");

		if(escapeStrList == null) escapeStrList = new String[] {"%", "_"};

		String escapeAt = "";

		if("mysql".equals(dbDriverId)) {
			escapeAt = "\\\\";
		} else {
			escapeAt = "\\";
		}

		String isEscapeStr = "N";

		for(int i = 0; i < str.length(); i++) {
			isEscapeStr = "N";
			for(String escapeStr : escapeStrList) {
				if(String.valueOf(str.charAt(i)).equals(escapeStr)){
					newStr.append(escapeAt);
					newStr.append(String.valueOf(str.charAt(i)));
					isEscapeStr = "Y";
				}
			}

			if("N".equals(isEscapeStr)) {
				newStr.append(String.valueOf(str.charAt(i)));
			}
		}

		return newStr.toString();
	}
}
