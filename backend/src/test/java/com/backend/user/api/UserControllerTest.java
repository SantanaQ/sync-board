package com.backend.user.api;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.user.application.UserService;
import com.backend.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getUser_returns_404_when_user_does_not_exist() throws Exception {

        UUID id = UUID.randomUUID();

        when(userService.getUser(id))
                .thenThrow(
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        mockMvc.perform(
                        get("/api/users/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void getUser_returns_user_response_when_user_exists() throws Exception {
        User user = new User(
                "test@example.com",
                "user",
                "password-hash"
        );

        UUID id = UUID.randomUUID();

        when(userService.getUser(id))
                .thenReturn(new UserResponse(
                        id,
                        user.displayName(),
                        user.email()
                ));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.email()))
                .andExpect(jsonPath("$.displayName").value(user.displayName()));
    }
}
