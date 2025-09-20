package com.cgmoffice.api.common.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cgmoffice.api.common.dto.AuthMenuDto;
import com.cgmoffice.api.common.dto.MenuDto;
import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.core.dao.AppDao;
import com.cgmoffice.core.exception.CmmnBizException;
import com.cgmoffice.core.utils.RequestUtils;
import com.cgmoffice.core.utils.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final AppDao appDao;
	private final PasswordEncoder passwordEncoder;
	private final MenuService menuService;

	/**
	 * 사용자 등록
	 * @param userDto
	 * @return
	 */
	public UserDto signup(UserDto userDto) {

		// userDto의 userId로 고객정보를 조회한다.
		if(appDao.selectOne("api.common.user.getUserInfo", userDto.getMemId()) != null) {
			throw new CmmnBizException("이미 등록되어있는 아이디입니다.");
		}

		// 가입되어 있지 않은 회원이면,
		// 권한 정보 만들고
		String authCd = "500";

		// SpringSecurityConfig 클래스에 선언된 passwordEncoder 메소드를 통해 bean 구현된 PasswordEncoder 을 호출해서 실행
		userDto.setPasswd(passwordEncoder.encode(userDto.getPasswd()));
		userDto.setUseYn("Y");
		userDto.setAuthCd(authCd);
		userDto.setCrtr(RequestUtils.getUser().getMemId());
		userDto.setAmdr(RequestUtils.getUser().getMemId());

		appDao.insert("api.common.user.insertUserInfo", userDto);

		return userDto;
	}

	/**
	 * 사용자 로그인 횟수와 최근 로그인 시간 업데이트
	 * @param username
	 * @return
	 */
	public int updateUserInfo(UserDto userDto) {
		// 사용자 로그인 횟수와 최근 로그인 시간 업데이트
		return appDao.update("api.common.user.updateUserInfo", userDto);
	}

	/**
	 * 사용자정보 가지고 오기
	 * @param username
	 * @return
	 */
	public UserDto getUserInfo(String memId) {
		// 사용자 정보 가지고 오기
		UserDto userDto = appDao.selectOne("api.common.user.getUserInfo", memId);

		if(userDto != null) {
			List<String> authGrpCd = appDao.selectList("api.common.user.getAuthGrpCd", userDto.getAuthCd());
			if(authGrpCd.size() > 0) {
				userDto.setAuthGrpCd(authGrpCd.get(0));
			}
			String comName = appDao.selectOne("api.common.user.getComName", userDto.getComCode());
			userDto.setComName(StringUtils.defaultIfEmpty(comName, ""));
		}

		return userDto;
	}

	/**
	 * 내정보 가지고 오기
	 * @return
	 */
	public UserDto getMyInfo() {

		UserDto userInfo = getUserInfo(SecurityUtil.getCurrentUsername());
		String authCd = userInfo.getAuthCd();
		if(StringUtils.isNoneEmpty(authCd)) {

			List<AuthMenuDto> authMenuList = menuService.getAuthMenuList(authCd);

			List<MenuDto> menuList_prev = menuService.getAllMenuList();

			List<MenuDto> menuList = new ArrayList<>();

			authMenuList.forEach(am -> {
				String menuCd = am.getMenuCd();

				MenuDto menu = menuList_prev.stream()
						.filter(a -> menuCd.equals(a.getMenuCd()))
						.findAny()
						.orElse(null);
				if(menu != null) {
					menu.setSelectYn(am.getSelectYn());
					menu.setUpdateYn(am.getUpdateYn());
					menu.setInsertYn(am.getInsertYn());
					menu.setDeleteYn(am.getDeleteYn());
					menu.setExcelYn(am.getExcelYn());
					menuList.add(menu);

					String upprMenuCd01 = menu.getUpprMenuCd();
					if(StringUtils.isNotEmpty(upprMenuCd01)  // 상위메뉴코드가 존재하고
							// menuList 상위메뉴코드에 해당하는 메뉴정보가 존재하지 않을 경우...
							&& !menuList.stream().anyMatch(a -> upprMenuCd01.equals(a.getMenuCd()))
							) {
						// 상위메뉴정보를 추출한다.
						MenuDto upMenu01 = menuList_prev.stream()
								.filter(a -> upprMenuCd01.equals(a.getMenuCd()))
								.findAny()
								.orElse(null);
						// 상위메뉴정보가 존재하면...
						if(upMenu01 != null) {
							menuList.add(upMenu01);

							String upprMenuCd02 = upMenu01.getUpprMenuCd();
							if(StringUtils.isNotEmpty(upprMenuCd02) // 상위메뉴코드가 존재하고
									// menuList 상위메뉴코드에 해당하는 메뉴정보가 존재하지 않을 경우...
									&& !menuList.stream().anyMatch(a -> upprMenuCd02.equals(a.getMenuCd()))
									) {
								MenuDto upMenu02 = menuList_prev.stream()
										.filter(a -> upprMenuCd02.equals(a.getMenuCd()))
										.findAny()
										.orElse(null);
								if(upMenu02 != null) {
									menuList.add(upMenu02);
								}
							}
						}
					}
				}
			});

			userInfo.setMenuList(menuList);
		}

		userInfo.setLgnScsYn("Y");

		RequestUtils.setAttribute("userDto", userInfo);

		return userInfo;
	}
}
