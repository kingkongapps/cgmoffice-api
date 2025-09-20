package com.cgmoffice._sample.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cgmoffice._sample.dto.Test01Dto;
import com.cgmoffice._sample.dto.Test02Dto;
import com.cgmoffice.core.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/httpcall")
@RequiredArgsConstructor
@Slf4j
public class SampleHttpCallController {

	private final WebClient webClient;

	private String baseUrl = "http://localhost:8082";

	@GetMapping("testGet")
	public Test01Dto testGet() {

		String urlString = new StringBuilder()
        		.append(baseUrl)
        		.append("/api/sample/httpcall/testGetResponse")
        		.append("?userName=마나님&userNickname=kkk")
        		.toString();

		Test01Dto rslt = webClient.get()
					        .uri(urlString)
//					        .uri(uriBuilder ->
//					        	uriBuilder.scheme("http") // 스키마 설정 (http/https)
//						            .host("localhost")   // 호스트 설정
//						            .port(8082)
//						            .path("/api/sample/webClient/testGetResponse") // 경로 설정
//						            .queryParam("userName", "마나님")
//						            .queryParam("userNickname", "kkk")
//						            .build()
//					        )
					        .retrieve() // 응답을 가져옴
					        /**
					         * bodyToMono 는 응답을 Mono로 변환하는 기능
					         *
					         * ## Mono와 Flux 이해하기
					         *  - Mono<T>: 하나의 결과를 반환 (ex: HTTP 요청의 단일 응답)
					         *  - Flux<T>: 여러 개의 결과를 반환 (ex: 스트림 데이터)
					         */
					        .bodyToMono(Test01Dto.class) // Test01Dto 형식으로 응답을 가지고 온다.
					        .block(); // 동기로 처리

		log.debug(">>> testGet 결과: {}", JsonUtils.toJsonStr(rslt));

		return rslt;
	}

	@GetMapping("testPost")
	public Test01Dto testPost() {

		String urlString = new StringBuilder()
        		.append(baseUrl)
        		.append("/api/sample/httpcall/testPostResponse")
        		.toString();

		Test02Dto reqDto = Test02Dto.builder()
				.userName("포스트테스터입니다.")
				.userNickname("별명입니다.")
				.build();

		Test01Dto rslt = webClient.post()
					        .uri(urlString)
					        .contentType(MediaType.APPLICATION_JSON)
					        .bodyValue(reqDto) // request body 에 데이터 셋팅
					        .retrieve() // 응답을 가져옴
					        .bodyToMono(Test01Dto.class) // Test01Dto 형식으로 응답을 가지고 온다.
					        .block(); // 동기로 처리

		log.debug(">>> testPost 결과: {}", JsonUtils.toJsonStr(rslt));

		return rslt;

	}

	@GetMapping("testGetResponse")
	public ResponseEntity<Test01Dto> testGetResponse(@Valid @ModelAttribute Test02Dto reqDto) {

		log.debug(">>> testGetResponse reqDto : {}", JsonUtils.toJsonStr(reqDto));

		Test01Dto rslt = Test01Dto.builder()
				.userAge(30)
				.userName("testGetResponse 결과")
				.build();

		return ResponseEntity.ok(rslt);
	}

	@PostMapping("testPostResponse")
	public ResponseEntity<Test01Dto> testPostResponse(@Valid @RequestBody Test02Dto reqDto) {

		log.debug(">>> testPostResponse reqDto : {}", JsonUtils.toJsonStr(reqDto));

		Test01Dto rslt = Test01Dto.builder()
				.userAge(30)
				.userName("testPostResponse 결과")
				.build();

		return ResponseEntity.ok(rslt);
	}

}
