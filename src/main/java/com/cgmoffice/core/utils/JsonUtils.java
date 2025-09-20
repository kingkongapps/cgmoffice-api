package com.cgmoffice.core.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Throwables;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JsonUtils {


	/**
	 * 클래스 객체를 json 보기좋은 문자열로 변환한다.
	 * @param target
	 * @return
	 */
	public String toJsonStrPretty(Object target) {
		if(target == null) {
			return "{}";
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.writeValueAsString(target);
		} catch(JsonProcessingException ex) {
			log.error(Throwables.getStackTraceAsString(ex));
		}
		return "{}";
	}

	/**
	 * 클래스 객체를 json 문자열로 변환한다.
	 * @param target
	 * @return
	 */
	public String toJsonStr(Object target) {
		if(target == null) {
			return "{}";
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		try {
			return mapper.writeValueAsString(target);
		} catch(JsonProcessingException ex) {
			log.error(Throwables.getStackTraceAsString(ex));
		}
		return "{}";
	}

	/**
	 * json문자열을 클래스 객체로 변환한다.
	 * @param <T>
	 * @param classpath 변환할 대상 클래스의 타입
	 * @param target 변환할 json String
	 * @return
	 */
	public <T> T fromJsonStr(Class<T> classpath, String target) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);   //선언한 필드만 매핑
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601로 출력
			return mapper.readValue(target, classpath);
		} catch (Exception e) {
			log.error(Throwables.getStackTraceAsString(e));
		}
		return null;
	}

	public <T> T fromJsonStr(TypeReference<T> typeRef, String json) {
        ObjectMapper mapper = new ObjectMapper();
	    try {
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601로 출력
	        return mapper.readValue(json, typeRef);
	    } catch (Exception e) {
	    	try {
                // 중첩된 JSON 문자열이라면 한 번 더 String으로 파싱 후 다시 시도
                String nested = mapper.readValue(json, String.class);
                return mapper.readValue(nested, typeRef);
            } catch (Exception e2) {
                log.error("Nested parse also failed: {}", e2.getMessage(), e2);
            }
	        log.error(Throwables.getStackTraceAsString(e));
	    }
	    return null;
	}

	/**
	 * json문자열을 jsonNode 로 변환한다.
	 * @param jsonStr json 문자열
	 * @return
	 */
	public JsonNode getJsonNode(String jsonStr) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(jsonStr);
		} catch (IOException ex) {
			log.error(Throwables.getStackTraceAsString(ex));
		}
		return null;
	}
}
