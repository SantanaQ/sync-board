package com.backend.auth.application;

import com.backend.auth.api.AuthResponse;
import com.backend.auth.api.LoginRequest;
import com.backend.auth.api.RegisterRequest;
import com.backend.auth.infrastructure.JwtService;
import com.backend.common.exception.ResourceAlreadyExistsException;
import com.backend.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_with_valid_registration_details_returns_jwt() {

        String rawPassword = "test";

        RegisterRequest request
                = new RegisterRequest("test@email.com", "test", rawPassword);

        String passwordHash = "hashed";

        String jwt = "valid-token";

        Authentication authentication = mock(Authentication.class);

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);
        when(passwordEncoder.encode(rawPassword))
                .thenReturn(passwordHash);
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(jwtService.generateToken(any()))
                .thenReturn(jwt);

        AuthResponse response = authService.register(request);

        assertThat(response.accessToken()).isEqualTo(jwt);

        verify(userRepository).existsByEmail(request.email());
        verify(jwtService).generateToken(any());
        verify(authenticationManager).authenticate(any());
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void register_with_duplicate_email_throws_resource_already_exists() {
        RegisterRequest request
                = new RegisterRequest("test@email.com", "test", "password");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(userRepository).existsByEmail(request.email());
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_with_valid_credentials_returns_jwt() {

        LoginRequest request = new LoginRequest("test@email.com", "test");

        Authentication authentication = mock(Authentication.class);

        String jwt = "valid-token";

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(jwtService.generateToken(authentication))
                .thenReturn(jwt);

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo(jwt);

        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateToken(authentication);
    }


}
