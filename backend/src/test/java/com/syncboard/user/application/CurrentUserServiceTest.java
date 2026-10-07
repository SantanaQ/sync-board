package com.syncboard.user.application;

import com.syncboard.TestDataFactory;
import com.syncboard.common.exception.InvalidCredentialsException;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CurrentUserService currentUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void get_with_valid_authentication_returns_user() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn(userId.toString());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User result = currentUserService.get();

        assertThat(result)
                .isSameAs(user);
    }

    @Test
    void get_when_authentication_is_null_throws_invalid_credentials_exception() {

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> currentUserService.get())
                .isInstanceOf(InvalidCredentialsException.class);
    }


    @Test
    void get_when_authentication_is_not_authenticated_throws_invalid_credentials_exception() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> currentUserService.get())
                .isInstanceOf(InvalidCredentialsException.class);
    }


    @Test
    void get_with_invalid_principal_throws_illegal_argument_exception() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn("not-a-uuid");

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> currentUserService.get())
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(userRepository);
    }


    @Test
    void get_when_user_does_not_exist_throws_resource_not_found_exception() {

        UUID userId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getName())
                .thenReturn(userId.toString());

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertThatThrownBy(() -> currentUserService.get())
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
