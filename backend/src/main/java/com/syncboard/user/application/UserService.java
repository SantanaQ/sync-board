package com.syncboard.user.application;

import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.user.api.UserResponse;
import com.syncboard.user.domain.User;
import com.syncboard.user.infrastructure.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public UserService(
            UserRepository userRepository,
            CurrentUserService currentUserService
    ) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
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

    public UserResponse me() {
        User user = currentUserService.get();

        return new UserResponse(
                user.id(),
                user.displayName(),
                user.email()
        );
    }

    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return Page.empty(pageable);
        }

        search = search.trim();

        return userRepository.findByDisplayNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search,
                search,
                pageable
        ).map((user) -> new UserResponse(
                user.id(),
                user.displayName(),
                user.email()
        ));
    }

}
