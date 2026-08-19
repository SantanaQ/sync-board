package com.backend.user.application;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.user.domain.User;
import com.backend.user.infrastructure.UserRepository;
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

    @InjectMocks
    private UserService userService;

    @Test
    void returns_user_when_user_exists() {
        UUID id = UUID.randomUUID();

        User user = new User(
                "test@example.com",
                "testuser",
                "hashed-password"
        );

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        User result = userService.getUser(id);

        assertThat(result).isSameAs(user);

        verify(userRepository).findById(id);
    }

    @Test
    void throws_resource_not_found_exception_when_user_does_not_exist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository).findById(id);
    }

}
