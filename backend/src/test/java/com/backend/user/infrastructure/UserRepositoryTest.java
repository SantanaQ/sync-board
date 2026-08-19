package com.backend.user.infrastructure;

import com.backend.RepositoryTestConfig;
import com.backend.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserRepositoryTest extends RepositoryTestConfig {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saves_user_and_finds_that_user_by_id() {

        User user = new User(
                "test@example.com",
                "testuser",
                "hashed-password"
        );

        User saved = userRepository.save(user);

        Optional<User> result = userRepository.findById(saved.id());

        assertThat(result)
                .isPresent();

        assertThat(result.get().email())
                .isEqualTo("test@example.com");
    }

    @Test
    void does_not_allow_duplicate_email() {
        userRepository.saveAndFlush(
                new User(
                        "test@example.com",
                        "user1",
                        "hash"
                )
        );

        assertThatThrownBy(() ->
                userRepository.saveAndFlush(
                        new User(
                                "test@example.com",
                                "user2",
                                "hash"
                        )
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void does_not_allow_null_email() {
        assertThatThrownBy(() ->
                userRepository.saveAndFlush(
                        new User(
                                null,
                                "user",
                                "hash"
                        )
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void does_not_allow_null_display_name() {
        assertThatThrownBy(() ->
                userRepository.saveAndFlush(
                        new User(
                                "test@example.com",
                                null,
                                "hash"
                        )
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void does_not_allow_null_password() {
        assertThatThrownBy(() ->
                userRepository.saveAndFlush(
                        new User(
                                "test@example.com",
                                "user",
                                null
                        )
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void user_is_saved_with_timestamps() {

        Instant before = Instant.now();

        User user = new User(
                "test@example.com",
                "testuser",
                "hashed-password"
        );

        User saved = userRepository.save(user);

        Optional<User> result = userRepository.findById(saved.id());

        Instant after = Instant.now();

        assertThat(result)
                .isPresent();

        assertThat(result.get().createdAt())
                .isBetween(before, after);

        assertThat(result.get().updatedAt())
                .isBetween(before, after);
    }
}
