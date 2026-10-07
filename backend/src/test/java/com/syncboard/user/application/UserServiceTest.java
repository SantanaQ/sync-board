package com.syncboard.user.application;

import com.syncboard.TestDataFactory;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.user.api.UserResponse;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_returns_user_when_user_exists() {
        UUID id = UUID.randomUUID();

        User user = new User(
                "test@example.com",
                "testuser",
                "hashed-password"
        );

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.getUser(id);

        assertThat(result.displayName()).isEqualTo(user.displayName());

        assertThat(result.email()).isEqualTo(user.email());

        verify(userRepository).findById(id);
    }

    @Test
    void getUser_throws_resource_not_found_exception_when_user_does_not_exist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository).findById(id);
    }

    @Test
    void me_with_current_user_returns_user_response() {

        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        when(currentUserService.get())
                .thenReturn(user);

        UserResponse response = userService.me();

        assertThat(response.id())
                .isEqualTo(user.id());

        assertThat(response.displayName())
                .isEqualTo(user.displayName());

        assertThat(response.email())
                .isEqualTo(user.email());
    }


    @Test
    void me_when_current_user_service_throws_exception_propagates_exception() {

        when(currentUserService.get())
                .thenThrow(new ResourceNotFoundException("User not found."));

        assertThatThrownBy(() -> userService.me())
                .isInstanceOf(ResourceNotFoundException.class);
    }


}
