package com.syncboard.user.application;

import com.syncboard.common.exception.InvalidCredentialsException;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("User is not authenticated.");
        }


        return userRepository.findById(UUID.fromString(authentication.getName()))
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found.")
                );
    }

}
