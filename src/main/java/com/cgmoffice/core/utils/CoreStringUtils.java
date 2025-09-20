package com.cgmoffice.core.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import lombok.experimental.UtilityClass;

/**
 *
 * @packageName : com.aia.core.util
 * @fileName	: AiakStringUtils.java
 * @author	  : E123531
 * @date		: 2022.10.21
 * @description : String 관련 함수
 * ===================================================================
 * DATE		 AUHTOR				   NOTE
 * ----------  --------------------- ---------------------------------
 * 2022.10.21  E123531			   최초 생성
 */
@UtilityClass
public class CoreStringUtils {

	public static final String WHITESPACE_STR_1 = " ";
	public static final String HANGUL_CHARS = ".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*";
	@SuppressWarnings("java:S5869")
	public static final Pattern REG_EXP_PTTERN = Pattern.compile(".*[\\[\\]=-_+\\\\?/()*.,]+.*$"); // 문자 '{', '}' 제외

	/**
	 * camel 케이스를 snake 케이스로 변환
	 * @param target
	 * @return
	 */
	public String camelToSnake(String target) {
		return target.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}

	/**
	 * snake 케이스를 camel 케이스로 변환
	 * @param target
	 * @return
	 */
	public String snakeToCamel(String target) {
		return CaseUtils.toCamelCase(target, false, new char[]{'_'});
	}


	/**
	 * 입력 문자열 한글 포함 여부
	 *
	 * @param inStr
	 * @return
	 */
	public boolean isHaveHangul(String inStr) {
		if(StringUtils.isBlank(inStr)) {
			return false;
		}
		return inStr.matches(HANGUL_CHARS);
	}

	/**
	 * <p>기준 문자열에 포함된 모든 대상 문자(char)를 제거한다.</p>
	 *
	 * <pre>
	 * AiakStringUtils.remove(null, *)	   = null
	 * AiakStringUtils.remove("", *)		 = ""
	 * AiakStringUtils.remove("queued", 'u') = "qeed"
	 * AiakStringUtils.remove("queued", 'z') = "queued"
	 * </pre>
	 *
	 * @param str	 입력 기준 문자열
	 * @param remove  입력받는 문자열에서 제거할 대상 문자
	 * @return 제거대상 문자열이 제거된 입력문자열 <br>
	 *		 <입력문자열이 null인 경우 출력문자열은 null>
	 */
	public String removeChar(String str, char remove) {
		if (StringUtils.isEmpty(str) || str.indexOf(remove) == -1) {
			return str;
		}
		char[] chars = str.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != remove) {
				chars[pos++] = chars[i];
			}
		}
		return new String(chars, 0, pos);
	}

	/**
	 * <p>문자열이 지정한 길이를 초과했을때 해당 문자열을 삭제, 길이가 짧은 경우 padStr문자열 padding</p>
	 *
	 * <pre>
	 * AiakStringUtils.cutAndPadding("1234567890", 10, AiakStringUtils.WHITESPACE_STR_1) = "1234567890"
	 * AiakStringUtils.cutAndPadding("12345"	 , 10, AiakStringUtils.WHITESPACE_STR_1) = "12345	 "
	 * AiakStringUtils.cutAndPadding("12345"	 , 10, "*")							  = "12345*****"
	 * </pre>
	 *
	 * @param srcStr 원본 문자열
	 * @param strLen 지정길이
	 * @param padStr padding 문자
	 * @return
	 */
	public String cutAndPadding(final String srcStr, final int strLen, final String padStr) {
		if (StringUtils.isEmpty(srcStr) || srcStr.length() > strLen) {
			return StringUtils.substring(srcStr, 0, strLen);
		}
		return StringUtils.isEmpty(padStr) ? StringUtils.substring(srcStr, 0, strLen) : StringUtils.rightPad(srcStr, strLen, padStr);
	}

	/**
	 * <p>문자열이 지정한 길이를 초과했을때 해당 문자열을 삭제, 길이가 짧은 경우 " " padding</p>
	 *
	 * <pre>
	 * AiakStringUtils.cutAndPadding("1234567890", 10) = "1234567890"
	 * AiakStringUtils.cutAndPadding("1234567890",  7) = "1234567"
	 * AiakStringUtils.cutAndPadding("12345"	 , 10) = "12345	 "
	 * </pre>
	 *
	 * @param srcStr 원본 문자열
	 * @param strLen 지정길이
	 * @return
	 */
	public String cutAndPadding(final String srcStr, final int strLen) {
		return cutAndPadding(srcStr, strLen, WHITESPACE_STR_1);
	}

	/**
	 * <p>
	 * 문자열 자르기(byte 단위) 한글포함된 경우 문자 깨짐 방지.
	 * </p>
	 *
	 * <pre>
	 * AiakStringUtils.subStringWithSuffix("abc_한글이름_", 7)  = "abc_한"
	 * AiakStringUtils.subStringWithSuffix("abc_한글이름_", 8)  = "abc_한"
	 * AiakStringUtils.subStringWithSuffix("abc_한글이름_", 10) = "abc_한글"
	 * AiakStringUtils.subStringWithSuffix("abc_한", 10)		= "abc_한"
	 * </pre>
	 *
	 * @param srcStr	 원본 문자열
	 * @param cutByteLen 남길 문자열의 길이 (한글:2Byte or 3Byte)
	 * @return
	 */
	public String subStringByte(final String srcStr, final int cutByteLen) {
		if(StringUtils.isEmpty(srcStr)) return srcStr;
		String rvStr = srcStr.trim();
		if(rvStr.getBytes().length <= cutByteLen) return rvStr;

		StringBuilder sb = new StringBuilder();
		int nCnt = 0;
		for(char ch : rvStr.toCharArray()) {
			nCnt += String.valueOf(ch).getBytes().length;
			if(nCnt > cutByteLen) break;
			sb.append(ch);
		}

		return sb.toString();
	}

	/**
	 * <p>문자열 자르고(byte 단위) suffix 문자열("...") 붙이기</p>
	 *
	 * <pre>
	 * AiakStringUtils.subStringByte4FixedSuffix("abc_한글이름_", 7)  = "abc_한..."
	 * AiakStringUtils.subStringByte4FixedSuffix("abc_한글이름_", 8)  = "abc_한..."
	 * AiakStringUtils.subStringByte4FixedSuffix("abc_한글이름_", 10) = "abc_한글..."
	 * </pre>
	 *
	 * @param srcStr	 원본 문자열
	 * @param cutByteLen 남길 문자열의 길이["..."제외] (한글:2Byte or 3Byte)
	 * @return
	 */
	public String subStringByte4FixedSuffix(final String srcStr, final int cutByteLen) {
		return subStringByte(srcStr, cutByteLen) + "...";
	}

	/**
	 * <p>문자열 자르고(byte 단위) suffix 문자열 붙이기</p>
	 *
	 * <pre>
	 * AiakStringUtils.subStringByte4Suffix("abc_한a",  7, '.') = "abc_한"
	 * AiakStringUtils.subStringByte4Suffix("abc_한a",  8, '.') = "abc_한a"
	 * AiakStringUtils.subStringByte4Suffix("abc_한a", 10, '.') = "abc_한a.."
	 * </pre>
	 *
	 * @param srcStr	 원본 문자열
	 * @param cutByteLen 남길 문자열의 길이[suffixStr 포함] (한글:2Byte or 3Byte)
	 * @param suffixChar 추가될 문자
	 * @return  "srcStr.length >= cutByteLen" 또는 "suffixChar 공백" 경우 subStringByte(srcStr, cutByteLen)와 동일
	 *
	 */
	public String subStringByte4Suffix(final String srcStr, final int cutByteLen, final Character suffixChar) {
		byte[] srcStrBytes = srcStr.getBytes();
		if(srcStrBytes.length >= cutByteLen || suffixChar == null) {
			return subStringByte(srcStr, cutByteLen);
		}
		int cutByteLength = srcStrBytes.length;
		int suffixLen = cutByteLen - cutByteLength;
		char[] suffixCharArr = new char[suffixLen];
		for(int i = 0; i < suffixLen; i++) {
			suffixCharArr[i] = suffixChar;
		}

		return subStringByte(srcStr, cutByteLength) + String.valueOf(suffixCharArr);
	}

	/**
	 * <p>camel case 형식의 문자열을 UnderLine 형식으로 변환</p>
	 *
	 * <pre>
	 * AiakStringUtils.camelToUnderLineString("CaseToStringS") = "case_to_string_s"
	 * </pre>
	 *
	 * @param str 원본 문자열
	 * @return
	 */
	public String camelToUnderLineString(String str) {
		return camelToFixedString(str, "_");
	}

	/**
	 * <p>camel case 형식의 문자열을 fixed 문자로 연결</p>
	 *
	 * <pre>
	 * AiakStringUtils.camelToFixedString("CaseToStringS", "+") = "case+to+string+s"
	 * </pre>
	 *
	 * @param str 원본 문자열
	 * @param fixed 연결 문자
	 * @return
	 */
	public String camelToFixedString(String str, String fixed) {
		str = StringUtils.trim(str);
		if (StringUtils.isEmpty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i != 0) {
					sb.append(fixed);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * <p>문자열을 Byte 단위 그룹으로 나누기. (한글 포함된 경우 깨지지 않게 분리!)</p>
	 *
	 * <pre>{@code
	 * String srcStr = "abc_12한글12345이름";
	 * int chunkSize = 6;
	 *
	 * List<String> result = AiakStringUtils.splitByteString(srcStr, chunkSize)
	 * result is [abc_12, 한글, 12345, 이름]
	 * }</pre>
	 *
	 * @param srcStr	원본 문자열
	 * @param chunkSize 나눌 문자열 그룹 길이
	 * @return
	 */
	public List<String> splitStringByte(String srcStr, int chunkSize) {
		List<String> rvList = new ArrayList<>();
		if(StringUtils.isBlank(srcStr)) {
			return rvList;
		}
		int srcLength = srcStr.getBytes().length;
		int beforeEndIndex = 0;
		for(int i = 0; i < srcLength; i += chunkSize) {
			String subStr = substringBytes(srcStr, i, Math.min(srcLength, i + chunkSize), beforeEndIndex);
			String[] splitStr = subStr.split("\\|");
			beforeEndIndex = Integer.parseInt(splitStr[1]);
			rvList.add(splitStr[0]);
		}
		return rvList;
	}

	private String substringBytes(String src, int bgBytes, int endBytes, int beforeEndIndex) {
		if(StringUtils.isBlank(src)) return "";
		if(bgBytes < 0) bgBytes = 0;
		if(endBytes < 1) return "";

		int len	  = src.length();
		int startIdx = -1;
		int endIdx   =  0;
		int curBytes =  0;
		String ch = null;
		for(int i = 0; i < len; i++) {
			ch = src.substring(i, i + 1);
			curBytes += ch.getBytes().length;

			if(startIdx == -1 && curBytes >= bgBytes ) {
				startIdx = i;
			}
			if(curBytes > endBytes) {
				break;
			} else {
				endIdx  = i + 1;
			}
		}
		if(beforeEndIndex >= startIdx) {
			startIdx = beforeEndIndex;
		}
		return src.substring(startIdx, endIdx ) + "|" + endIdx ;
	}

	/**
	 * 정규식 Pattern 여부 체크.
	 * 문자 '{', '}' 제외 - Error string Array binding할때 사용 ex){0},{1}
	 */
	public boolean isRegExp(final String str) {
		try {
			return REG_EXP_PTTERN.matcher(str).matches();
		} catch(PatternSyntaxException pse) {
			return false;
		}
	}


	/**
	 * @param source
	 * @return XSS Encoding
	 */
	public String encodeXSS(String source) {

		// 우리FIS 기준 XSS 필터 적용
		source = filterXSSForWoori(source);
		return source;

		// untrustedStringForEncoding 필터 적용
//		int sourceLength = source.length();
//		StringBuilder sb = new StringBuilder();
//
//		for(int i=0; i<sourceLength; i++) {
//			Character c = source.charAt(i);
//			String untrustedData = simpleEcodeXSS(c.toString());
//			if(untrustedData == null) sb.append(c);
//			else sb.append(untrustedData);
//		}
//
//		return sb.toString();
	}

	/**
	 * 우리FIS 기준 XSS 필터
	 * @param value
	 * @return
	 */
	public String filterXSSForWoori(String value) {
		if (value != null && !"".equals(value)) {

			// sql injection 방지
			value = value.replaceAll("'", "&#x27;");

			//remove <script>...</script>
			Pattern pattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			value = pattern.matcher(value).replaceAll("");

			//remove src='....'
//			pattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//			value = pattern.matcher(value).replaceAll("");

			//remove </script>
			pattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			value = pattern.matcher(value).replaceAll("");

			//remove <script...>
			pattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = pattern.matcher(value).replaceAll("");

			//remove eval(...)
			pattern = Pattern.compile("eval[\\s]*\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = pattern.matcher(value).replaceAll("");

			//remove expression(...)
			pattern = Pattern.compile("expression[\\s]*\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = pattern.matcher(value).replaceAll("");

			//remove javascript:
			pattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = pattern.matcher(value).replaceAll("");

			//remove vbscript:
			pattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = pattern.matcher(value).replaceAll("");

//			//remove <body> , <embed> , <frame> ... <div>
//			pattern = Pattern.compile("<(body|embed|frame|script|link|iframe|object|style|frameset|meta|img|div)[\\s]*[^>]*[\\s]*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//			value = pattern.matcher(value).replaceAll("");

			//remove <body> , <embed> , <frame> ... <div>
			pattern = Pattern.compile("<(body|embed|frame|script|link|iframe|object|style|frameset|meta)[\\s]*[^>]*[\\s]*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = pattern.matcher(value).replaceAll("");

			//remove html event handler
			//remove on...=...
//			pattern = Pattern.compile("on[a-z]+[^\\S\n]*=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = pattern.matcher(value).replaceAll("");
		} else {
			value = "";
		}
		return value;
	}

	/**
	 * 16진수를 2진수로 변환
	 * @param target
	 * @return
	 */
	public static String hexToBin(String hex) {
		return new BigInteger(hex, 16).toString(2);
	}

}
