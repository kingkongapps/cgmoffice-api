package com.cgmoffice.api.common.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.cgmoffice.api.common.dto.CmmnPropertiesDto;
import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.jwt.TokenProvider;
import com.cgmoffice.core.properties.CmmnProperties;
import com.cgmoffice.core.utils.CoreUtils;
import com.cgmoffice.core.utils.CmmnMap;
import com.cgmoffice.core.utils.MessageI18nUtils;
import com.cgmoffice.core.utils.RequestUtils;
import com.cgmoffice.core.utils.SecurityUtil;
import com.cgmoffice.core.utils.SessionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("userDetailsService")
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
	private final AppDao appDao;
	private final UserService userService;
	private final HttpServletRequest request;
	private final CmmnProperties cmmnProperties;
	private final TokenProvider tokenProvider;

	@Value("${server.url}")
	private String serverUrl;

	@Override
	// 로그인시에 DB에서 유저정보와 권한정보를 가져와서 해당 정보를 기반으로 userdetails.User 객체를 생성해 리턴
	public UserDetails loadUserByUsername(final String memId) {

		// DB에서 사용자정보를 추출한다.
		UserDto userDto = userService.getUserInfo(memId);
		if(userDto == null) {
			throw new CmmnBizException(MessageI18nUtils.getMessage("global.bad-credential-1.message"));
		}

		// UserDetails 형식의 사용자정보를 생성해서 리턴한다.
		return createUser(userDto);
	}

	private UserDetails createUser(UserDto userDto) {

		// 사용자가 활성상태가 아닌경우
		if (!"Y".equals(userDto.getUseYn())) {

			String[] params = {userDto.getMemId()};
			throw new CmmnBizException(
					MessageI18nUtils.getMessage("global.bad-credential-2.message", params)
					);
		}

		// UserDetails 형식의 사용자정보에 셋팅할 권한정보를 생성한다.
		List<GrantedAuthority> grantedAuthorities
			= Arrays.asList(new SimpleGrantedAuthority(userDto.getMemId()));

		// UserDetails 형식의 사용자정보를 생성해서 리턴한다.
		return  User.builder()
				.username(userDto.getMemId())
				.password(userDto.getPasswd())
				.authorities(grantedAuthorities)
				.build();
	}

	public void insertLoginLog(UserDto loginDto) {

		String memId = loginDto.getMemId();
		String lgnScsYn = loginDto.getLgnScsYn();

		CmmnMap params = new CmmnMap()
				.put("MEM_ID", memId)
				.put("LOGIN_CFCD", "1")
				.put("LGN_SCS_YN", lgnScsYn)
				.put("ACS_DT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))
				.put("ACS_IP", CoreUtils.getIpAddr(request))
				.put("CRTR", memId)
				.put("AMDR", memId)
				;
		loginInsert(params);
	}

	public void insertLogOutLog() {

		// DB에서 사용자정보를 추출한다.
		UserDto userDto = userService.getUserInfo(SecurityUtil.getCurrentUsername());

		String memId = userDto.getMemId();

		CmmnMap params = new CmmnMap()
				.put("MEM_ID", memId)
				.put("LOGIN_CFCD", "2")
				.put("LGN_SCS_YN", "Y")
				.put("ACS_DT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))
				.put("ACS_IP", CoreUtils.getIpAddr(request))
				.put("CRTR", memId)
				.put("AMDR", memId)
				;
		loginInsert(params);
	}

	private void loginInsert(CmmnMap params) {
		appDao.insert("api.common.auth.insertLoginLog", params);
	}

	public UserDto checkFindAcYn() {

		return appDao.selectOne("api.common.auth.getFindAcYn_TB_MEM", RequestUtils.getUser().getMemId());
	}

	/**
	*
	* CmmnProperties 정보 가져오기 [필요한 정보만]
	* @param void
	* @return CmmnPropertiesDto
	*/
	public CmmnPropertiesDto getCmmnProperties(HttpServletRequest request, HttpServletResponse response) {
		CmmnPropertiesDto dto = new CmmnPropertiesDto();

//		dto.setRscVer(cmmnProperties.getRscVer());
//		dto.setEhcacheConfig(cmmnProperties.getEhcacheConfig());
//
//		dto.setQrUrl(cmmnProperties.getQrUrl());
//		dto.setFileMng(cmmnProperties.getFileMng());
//		dto.setAes256Key(cmmnProperties.getAes256Key());
		dto.setActive(cmmnProperties.getActive());
//		dto.setPdfDir(cmmnProperties.getPdfDir());

//		dto.setAppDriverClassName(cmmnProperties.getAppDriverClassName());
//		dto.setAppDatabaseId(cmmnProperties.getAppDatabaseId());
//
//		dto.setModuleDriverClassName(cmmnProperties.getModuleDriverClassName());
//		dto.setModuleDatabaseId(cmmnProperties.getModuleDatabaseId());
//
//		dto.setJwt(cmmnProperties.getJwt());
//		dto.setHttpCall(cmmnProperties.getHttpCall());
//		dto.setScheduleSetting(cmmnProperties.getScheduleSetting());

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String jwt = CoreUtils.resolveToken(request, response);

		// 로그인이 되어있는 경우
		if(authentication != null
				&& StringUtils.isNotEmpty(jwt)
				&& tokenProvider.validateToken(jwt)) {
			cmmnProperties.setIsLogin("Y");
		// 로그인이 되어있지 않은 경우
		} else {
			cmmnProperties.setIsLogin("N");
		}

		UserDto userInfo = userService.getUserInfo(SecurityUtil.getCurrentUsername());

		if(userInfo != null) {
			String url = serverUrl + "static/css/styles_" + userInfo.getComCode() + ".css";

			URL conUrl = null;
			try {
				conUrl = new URL(url);
				HttpURLConnection connection = null;
				try {
					connection = (HttpURLConnection) conUrl.openConnection();
					connection.setRequestMethod("GET");
					connection.connect();

					int code = connection.getResponseCode();

					if(code == HttpURLConnection.HTTP_OK) {
						cmmnProperties.setComCode(userInfo.getComCode());
					} else {
						cmmnProperties.setComCode("CMMN");
					}
				} catch (IOException e) {
					log.error(e.toString());
				} finally {
					if(connection != null) {
						connection.disconnect();
					}
				}
			} catch (MalformedURLException e) {
				log.error(e.toString());
			}
		} else {
			cmmnProperties.setComCode("CMMN");
		}

		SessionUtils.setAttribute("properties", cmmnProperties);

		return dto;
	}
}
