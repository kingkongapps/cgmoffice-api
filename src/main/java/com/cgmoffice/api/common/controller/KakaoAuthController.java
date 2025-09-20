package com.cgmoffice.api.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/common/kakaoauth")
@RequiredArgsConstructor
public class KakaoAuthController {

	@GetMapping("001")
	public String test001() {

		System.out.print("111111");
		System.out.print("222222");
		System.out.print("333333");

		return "<script>location.href='kakaotalk://kakaopay/cert/sign?tx_id=09599e31cc-7581-43e2-94c00b86f61ca636'</script>";
	}

	@GetMapping("002")
	public String test002() {
		return "test002";
	}

}
