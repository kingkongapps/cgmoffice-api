package com.cgmoffice.core.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SecurityUtil {

    public String getCurrentUsername() {

        // authentication객체가 저장되는 시점은 JwtFilter의 doFilter 메소드에서
        // Request가 들어올 때 SecurityContext에 Authentication 객체를 저장해서 사용
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("Security Context에 인증 정보가 없습니다.");
            return null;
        }

        String username = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
        	UserDetails springSecurityUser = (UserDetails)principal;
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return username;
    }

}
