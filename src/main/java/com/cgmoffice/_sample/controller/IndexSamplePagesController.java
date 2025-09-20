package com.cgmoffice._sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("sample")
@Controller
public class IndexSamplePagesController {

	/**
	 * 메인페이지
	 * @return
	 */
	@GetMapping({"/", ""})
	public String SampleMainPage() {

		return "sample/SampleMain";
	}

	/**
	 * 로그인페이지
	 * @return
	 */
	@GetMapping("sampleLogin")
	public String sampleLogin() {

		return "sample/SampleLogin";
	}

	/**
	 * 회원가입페이지
	 * @return
	 */
	@GetMapping("sampleSignup")
	public String sampleSignup() {

		return "sample/SampleSignup";
	}

	@GetMapping("samplePaging")
	public String samplePaging() {

		return "sample/SamplePaging";
	}

	@GetMapping("sampleExcelMng")
	public String sampleExcelPage() {

		return "sample/SampleExcelMng";
	}

	@GetMapping("sampleFileMng")
	public String sampleFileMng() {

		return "sample/SampleFileMng";
	}

	@GetMapping("sampleImgMng")
	public String sampleImgMng() {

		return "sample/SampleImgMng";
	}

	@GetMapping("samplePdfMng")
	public String samplePdfMng() {

		return "sample/SamplePdfMng";
	}

	@GetMapping("sampleHttpCall")
	public String sampleHttpCall() {

		return "sample/SampleHttpCall";
	}

	@GetMapping("sampleQr")
	public String sampleQr() {

		return "sample/SampleQr";
	}

	@GetMapping("sampleVideo")
	public String sampleVideo() {

		return "sample/SampleVideo";
	}

	@GetMapping("sampleAudio")
	public String sampleAudio() {

		return "sample/SampleAudio";
	}

	@GetMapping("sampleJsCommon")
	public String sampleJsCommon() {

		return "sample/SampleJsCommon";
	}



}
