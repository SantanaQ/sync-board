package com.syncboard.auth.application;

import com.syncboard.TestDataFactory;
import com.syncboard.auth.api.LoginRequest;
import com.syncboard.auth.api.RegisterRequest;
import com.syncboard.auth.infrastructure.JwtService;
import com.syncboard.common.exception.ResourceAlreadyExistsException;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.user.api.UserResponse;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthCookieService authCookieService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;


    @Test
    void register_with_valid_registration_details_sets_auth_cookie() {

        String rawPassword = "test";

        RegisterRequest request =
                TestDataFactory.registerRequest(rawPassword);

        String passwordHash = "hashed";
        String jwt = "valid-token";

        Authentication authentication = mock(Authentication.class);

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(passwordHash);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn(jwt);

        authService.register(request, response);

        verify(userRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(rawPassword);
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(authentication);

        verify(authCookieService)
                .setAuthCookie(response, jwt);
    }


    @Test
    void register_with_duplicate_email_throws_resource_already_exists() {

        RegisterRequest request =
                TestDataFactory.registerRequest("password");

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThatThrownBy(
                () -> authService.register(request, response)
        )
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(userRepository)
                .existsByEmail(request.email());

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(authCookieService);
    }


    @Test
    void login_with_valid_credentials_sets_auth_cookie() {

        LoginRequest request =
                new LoginRequest("test@email.com", "test");

        Authentication authentication =
                mock(Authentication.class);

        String jwt = "valid-token";

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn(jwt);

        authService.login(request, response);

        verify(authenticationManager)
                .authenticate(any());

        verify(jwtService)
                .generateToken(authentication);

        verify(authCookieService)
                .setAuthCookie(response, jwt);
    }


    @Test
    void me_with_not_existing_principal_throws_resource_not_found() {

        UUID userId = UUID.randomUUID();

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn(userId.toString());

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> authService.me(authentication)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void me_with_invalid_principal_throws_illegal_argument_exception() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("not-a-uuid");

        assertThatThrownBy(
                () -> authService.me(authentication)
        )
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void me_with_valid_authentication_returns_user() {

        UUID userId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn(userId.toString());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserResponse response =
                authService.me(authentication);

        assertThat(response.email())
                .isEqualTo(user.email());
    }


    @Test
    void logout_clears_auth_cookie() {

        authService.logout(response);

        verify(authCookieService)
                .clearAuthCookie(response);
    }
}