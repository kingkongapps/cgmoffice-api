package com.cgmoffice.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"", "/"})
public class UiController {

	@GetMapping
	public String openPage_main() {
		return "admin/Index";
	}

	@GetMapping("CMN{code}")
	public String openPage_CMN(@PathVariable String code) {
		return new StringBuilder()
				.append( "admin/cmn/CMN")
				.append(code)
				.toString();
	}

	@GetMapping("SYS{code}")
	public String openPage_SYS(@PathVariable String code) {
		return new StringBuilder()
				.append( "admin/sys/SYS")
				.append(code)
				.toString();
	}

	@GetMapping("CNT{code}")
	public String openPage_CNT(@PathVariable String code) {
		return new StringBuilder()
				.append( "admin/cnt/CNT")
				.append(code)
				.toString();
	}

	@GetMapping("CUC{code}")
	public String openPage_CUC(@PathVariable String code) {
		return new StringBuilder()
				.append( "admin/cuc/CUC")
				.append(code)
				.toString();
	}

	@GetMapping("IDC{code}")
	public String openPage_IDC(@PathVariable String code) {
		return new StringBuilder()
				.append( "admin/idc/IDC")
				.append(code)
				.toString();
	}

	@GetMapping("FLE{code}")
	public String openPage_FLE(@PathVariable String code) {
		return new StringBuilder()
				.append( "admin/fle/FLE")
				.append(code)
				.toString();
	}
}
