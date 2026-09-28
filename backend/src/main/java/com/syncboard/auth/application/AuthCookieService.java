package com.syncboard.auth.application;

import com.syncboard.config.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthCookieService {

    JwtProperties jwtProperties;

    public AuthCookieService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public void setAuthCookie(
            HttpServletResponse response,
            String accessToken
    ) {
        ResponseCookie cookie = ResponseCookie.from(
                        "access_token",
                        accessToken
                )
                .httpOnly(true)
                .secure(jwtProperties.cookieSecure())
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtProperties.expiration())
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    public void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }
}
