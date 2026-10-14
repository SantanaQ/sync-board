package com.syncboard.user.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RestController;
import com.syncboard.user.application.UserService;
import org.springframework.web.bind.annotation.*;

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
        return userService.getUser(id);
    }

    @GetMapping("/me")
    public UserResponse me() {
        return userService.me();
    }

    @GetMapping
    public Page<UserResponse> searchUsers(
            @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return userService.searchUsers(search, pageable);
    }
}
