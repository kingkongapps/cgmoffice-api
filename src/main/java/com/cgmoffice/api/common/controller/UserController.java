package com.cgmoffice.api.common.controller;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgmoffice.api.common.dto.UserDto;
import com.cgmoffice.api.common.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common/user")
public class UserController {

    private final UserService userService;

    /**
     * 회원등록
     * @param userDto
     * @return
     */
    @PostMapping("/signup")
    public UserDto signup(@Valid @RequestBody UserDto userDto) {
        return userService.signup(userDto);
    }

    /**
     * 사용자정보 조회
     * @return
     */
    @GetMapping("/info")
    public UserDto getMyInfo() {
        return userService.getMyInfo();
    }

    /**
     * 사용자정보 조회
     * @param userId
     * @return
     */
    @GetMapping("/getinfo/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public UserDto getUserInfo(@PathVariable String userId) {
        return userService.getUserInfo(userId);
    }

}
