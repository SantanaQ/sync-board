package com.syncboard.auth.application;

import com.syncboard.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class AuthCookieServiceTest {

    private AuthCookieService authCookieService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(
                "test-secret",
                Duration.ofHours(8),
                false
        );

        authCookieService = new AuthCookieService(jwtProperties);
    }

    @Test
    void setAuthCookie_sets_access_token_cookie() {

        MockHttpServletResponse response = new MockHttpServletResponse();

        String jwt = "valid-token";

        authCookieService.setAuthCookie(response, jwt);

        String cookie = response.getHeader("Set-Cookie");

        assertThat(cookie)
                .contains("access_token=" + jwt)
                .contains("HttpOnly")
                .contains("SameSite=Lax")
                .contains("Path=/")
                .contains("Max-Age=28800");
    }

    @Test
    void clearAuthCookie_clears_access_token_cookie() {

        MockHttpServletResponse response = new MockHttpServletResponse();

        authCookieService.clearAuthCookie(response);

        String cookie = response.getHeader("Set-Cookie");

        assertThat(cookie)
                .contains("access_token=")
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Lax")
                .contains("Path=/")
                .contains("Max-Age=0");
    }
}
