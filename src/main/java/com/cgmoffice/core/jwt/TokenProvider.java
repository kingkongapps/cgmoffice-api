package com.cgmoffice.core.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {

	private static final String AUTHORITIES_KEY = "auth";
	public static final String AUTH_STR_BEARER = "Bearer ";

	@Value("${cmmn-properties.jwt.token-validity-in-seconds}")
	private long tokenValidityInSeconds;

	private static final SecretKey secretKey = Keys.hmacShaKeyFor(
			new byte[64] // HMAC-SHA512에 적합한 512비트(64바이트) 크기
					);

	public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰의 expire 시간을 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + (this.tokenValidityInSeconds * 1000));

        return Jwts.builder()
        		.subject(authentication.getName()) // 사용자 로그인 아이디
                .claim(AUTHORITIES_KEY, authorities) // 정보 저장
                .signWith(secretKey)
                .expiration(validity)
                .compact();
	}

	// 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public Authentication getAuthentication(String jwt) {
        Claims claims = Jwts
				.parser()
				.verifyWith(secretKey) // 시크릿 키를 넣어주어 토큰을 검증할 수 있습니다.
				.build()
				.parseSignedClaims(jwt) // 해석할 토큰을 문자열(String) 형태로 넣어줍니다.
				.getPayload();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    /**
     * jwt 토큰검증 수행
     * @param jwt
     * @return
     */
	public boolean validateToken(String jwt) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error(">>> 잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error(">>> 만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error(">>> 지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error(">>> JWT 토큰이 잘못되었습니다.");
        }
        return false;
	}
}
