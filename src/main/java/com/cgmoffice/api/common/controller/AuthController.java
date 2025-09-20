package com.cgmoffice.api.common.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.CmmnPropertiesDto;
import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.api.common.service.AuthService;
import com.cgmoffice.api.common.service.UserService;
import com.cgmoffice.core.jwt.TokenProvider;
import com.cgmoffice.core.utils.RequestUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/common/authenticate")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final UserService userService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	/**
	 *
	 * 로그인시 사용자 인가 체크 및 token 발급
	 * @param loginDto
	 * @return
	 */
	@PostMapping("signin")
	public ResponseEntity<UserDto> signin(@Valid @RequestBody UserDto loginDto, HttpServletResponse response) {

		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(
						loginDto.getMemId(), // 로그인요청 사용자아이디
						loginDto.getPasswd()  // 로그인요청 사용자비밀번호
						);

		RequestUtils.setAttribute("userDto", loginDto);

		Authentication authentication = authenticationManagerBuilder
				// AuthenticationManager 객체를 가져옵니다.
				.getObject()
				// authenticate 호출시 DaoAuthenticationProvider 를 사용하며, 여기서
				// UserDetailsService.loadUserByUsername(AuthService class의 loadUserByUsername 메소드)를 implement 한 클래스가 호출되어, 사용자의 인증 정보를 데이터베이스나 다른 저장소에서 로드합니다.
				// loadUserByUsername 로 로드된 사용자정보를 토대로,
				// 내부적으로 DaoAuthenticationProvider 클래스에서 (additionalAuthenticationChecks 메소드 에서) 로드된 사용자 정보의 비밀번호와 입력된 비밀번호를 PasswordEncoder.matches() 메서드를 사용해 비교한다.
				// 검증 성공 시 Authenticated Authentication 객체 반환하고, 검증 실패 시 BadCredentialsException 또는 다른 예외를 발생한다.
				.authenticate(authenticationToken);

		// 해당 객체를 SecurityContextHolder에 저장하고
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
		String jwt = tokenProvider.createToken(authentication);

		HttpHeaders httpHeaders = new HttpHeaders();
		// response header에 jwt token에 넣어줌
//		httpHeaders.add(HttpHeaders.AUTHORIZATION, TokenProvider.AUTH_STR_BEARER + jwt);
		httpHeaders.set("jwt", jwt);

		Cookie cookie = new Cookie("jwt", jwt);
		cookie.setHttpOnly(true); // JS에서 접근 못 하게
//		cookie.setSecure(true); // HTTPS일 때만 전송
		cookie.setPath("/"); // 전체 경로에 대해 유효
		response.addCookie(cookie);

		loginDto.setLgnScsYn("Y");

		// 로그인로그 삽입
		authService.insertLoginLog(loginDto);

		// 사용자 로그인 횟수와 최근 로그인 시간 업데이트
		userService.updateUserInfo(loginDto);

		return ResponseEntity.ok()
				.headers(httpHeaders)
				.build()
				;
	}

	@GetMapping("verify")
	public ResponseEntity<Void> authVerify() {
		return ResponseEntity.ok().build();
	}

	@GetMapping("signout")
	public ResponseEntity<Void> signout(HttpServletResponse response) {

		Cookie cookie = new Cookie("jwt", "");
		cookie.setHttpOnly(true); // JS에서 접근 못 하게
		cookie.setPath("/"); // 전체 경로에 대해 유효
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("jwt", "");

		// 로그인로그 삽입
		authService.insertLogOutLog();

		return ResponseEntity.ok().headers(httpHeaders).build();
	}

	/**
	*
	* 초기비밀번호 변경 여부 조회
	* @param UserDto
	* @return UserDto
	*/
	@PostMapping("checkFindAcYn")
	public UserDto checkFindAcYn() {
		return authService.checkFindAcYn();
	}

	/**
	*
	* CmmnProperties 정보 가져오기 [필요한 정보만]
	* @param void
	* @return CmmnPropertiesDto
	*/
	@PostMapping("getCmmnProperties")
	public CmmnPropertiesDto getCmmnProperties(HttpServletRequest request, HttpServletResponse response) {
		return authService.getCmmnProperties(request, response);
	}
}
