package com.cgmoffice._sample.controller;

import java.sql.SQLException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice._sample.dto.Test02Dto;
import com.cgmoffice.core.constant.BaseResponseCode;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.utils.CmmnMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/exception")
@RequiredArgsConstructor
@Slf4j
public class SampleExceptionController {

	/**
	 * Dto Valid 동작검증
	 * @param dto
	 * @return
	 * @throws SQLException
	 */
    @GetMapping("/test07")
    public String test07(@Valid @ModelAttribute Test02Dto dto) throws SQLException {

    	log.debug(">>> test07 dto : {}", dto);

    	return "SUCC";
    }

	/**
	 * Dto Valid 동작검증
	 * @param dto
	 * @return
	 * @throws SQLException
	 */
    @PostMapping("/test08")
    public String test08(@Valid @RequestBody Test02Dto dto) throws SQLException {

    	log.debug(">>> test08 dto : {}", dto);

    	return "SUCC";
    }

    @GetMapping("/test01")
    public CmmnMap test01(CmmnMap params) {

    	throw new CmmnBizException();
    }

    @GetMapping("/test02")
    public CmmnMap test02(CmmnMap params) {

		throw CmmnBizException.builder()
	           .httpStatusCode(HttpStatus.BAD_REQUEST.value())
	           .responseCode(BaseResponseCode.FK40101.code())
	           .message(BaseResponseCode.FK40101.msg())
	           .build();
    }

    @GetMapping("/test03")
    public CmmnMap test03(CmmnMap params) {

    	try {
    		throw new RuntimeException("테스트 런타임 에러입니다.!!!");
    	} catch (Exception e) {
        	throw new CmmnBizException(e.getMessage());
		}

    }

    @GetMapping("/test04")
    public CmmnMap test04(CmmnMap params) {

    	try {
    		throw new RuntimeException("test04 테스트 런타임 에러입니다.!!!");
    	} catch (Exception e) {
    		// exception 원인도 같이 보낸다.
    		// 서버콘솔창에는 원인 exception 이 찍히고, 클라이언트단에는 셋팅한 메세지가 전송된다.
        	throw new CmmnBizException("알수없는 에러가 발생했습니다.", e);
		}

    }

    @GetMapping("/test05")
    public CmmnMap test05(CmmnMap params) {

    	try {
    		throw new SQLException("test05 테스트 런타임 에러입니다.!!!");
    	} catch (Exception e) {
    		// exception 원인도 같이 보낸다.
    		// 서버콘솔창에는 원인 exception 이 찍히고, 클라이언트단에는 셋팅한 메세지가 전송된다.
        	throw new CmmnBizException(e.getMessage(), e);
		}

    }

    @GetMapping("/test06")
    public CmmnMap test06(CmmnMap params) throws SQLException {

    	throw new SQLException("test06 테스트 SQL 에러입니다.!!!");
    }
}
