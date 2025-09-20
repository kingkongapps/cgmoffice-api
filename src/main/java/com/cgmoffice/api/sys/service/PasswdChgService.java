package com.cgmoffice.api.sys.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cgmoffice.api.sys.dto.PasswdChgReqDto;
import com.cgmoffice.api.sys.dto.PasswdChgResDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswdChgService {

	private final AppDao appDao;
	private final PasswordEncoder passwordEncoder;

	//비밂번호 변경
	public PasswdChgResDto updPasswd(PasswdChgReqDto dto){

		PasswdChgResDto res = new PasswdChgResDto();

		//사용자 아이디
		dto.setUserId(RequestUtils.getUser().getMemId());

		//현재 비밀번호 검증
		if(verifyPasswd(dto)) {

			//비밀번호 변경
			if(chgPasswd(dto)) {
				res.setReturnCd("S");
				res.setReturnMsg("비밀번호를 변경하였습니다.");
			}

		} else {
			res.setReturnCd("E");
			res.setReturnMsg("현재 비밀번호가 틀렸습니다.");
		}

		return res;
	}

	//현재 비밀번호 검증
	public boolean verifyPasswd(PasswdChgReqDto dto) {

		String passwd = appDao.selectOne("api.sys.PasswdChg.getPasswd_TB_MEM", dto.getUserId());
		String pwCur = dto.getPwCur();

		if(passwordEncoder.matches(pwCur, passwd)) {
			return true;
		}

		return false;
	}

	//비밀번호 변경
	public boolean chgPasswd(PasswdChgReqDto dto) {

		dto.setEncodedPw(passwordEncoder.encode(dto.getPwNew()));

		if(0 < appDao.update("api.sys.PasswdChg.update_TB_MEM", dto)) {
			return true;
		}

		return false;
	}

}
