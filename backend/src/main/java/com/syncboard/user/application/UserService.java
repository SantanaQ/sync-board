package com.syncboard.user.application;

import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.user.api.UserResponse;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "User with id" + id + " not found."
                        )
                );
        return new UserResponse(
                user.id(),
                user.displayName(),
                user.email()
        );
    }

}
