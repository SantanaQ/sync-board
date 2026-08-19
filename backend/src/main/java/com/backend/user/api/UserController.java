package com.backend.user.api;

import org.springframework.web.bind.annotation.RestController;
import com.backend.user.application.UserService;
import com.backend.user.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        User user = userService.getUser(id);

        return new UserResponse(
                user.id(),
                user.displayName(),
                user.email()
        );
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        List<User> users = userService.getUsers();
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(new UserResponse(
                    user.id(),
                    user.displayName(),
                    user.email()
            ));
        }
        return responses;
    }




}
