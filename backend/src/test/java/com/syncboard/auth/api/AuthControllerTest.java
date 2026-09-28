package com.syncboard.auth.api;

import com.syncboard.auth.application.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void invalid_email_in_registration_is_not_accepted() throws Exception {

        String json = """
            {
                "email": "foo",
                "displayName": "user",
                "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void blank_email_in_registration_is_not_accepted() throws Exception {
        String json = """
            {
                "email": "",
                "displayName": "user",
                "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void blank_displayName_in_registration_is_not_accepted() throws Exception {
        String json = """
            {
                "email": "test@mail.com",
                "displayName": "",
                "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void blank_password_in_registration_is_not_accepted() throws Exception {
        String json = """
            {
                "email": "test@mail.com",
                "displayName": "user",
                "password": ""
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void password_with_size_smaller_than_eight_in_registration_is__not_accepted() throws Exception {
        String json = """
            {
                "email": "test@mail.com",
                "displayName": "user",
                "password": "1234"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void password_with_size_greater_than_one_hundred_in_registration_is__not_accepted() throws Exception {
        String json = """
            {
                "email": "test@mail.com",
                "displayName": "user",
                "password": "knTm1FgjvNhtgVJfjrrfKeMQqtacWn0KkKK0N8zRVZFfLhWtxhpa9wMehJUYvK3jZvpijAAE0gjwtfWJgGFMkFEGqKYFn3inPHKb3"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void displayName_with_size_greater_than_one_hundred_in_registration_is__not_accepted() throws Exception {
        String json = """
            {
                "email": "test@mail.com",
                "displayName": "iQjfMbAaYM2mHhkgiqAgBXkwQtRS5WaPkhJEkBNFUAShRMZ3U036x6rbCddx24S1HqZQZZcR02vc5XD16D7nxF26jqFCHB3tr6jHg",
                "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void blank_password_in_login_is_not_accepted() throws Exception {
        String json = """
            {
                "email": "test@mail.com",
                "displayName": "user",
                "password": ""
            }
            """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void blank_email_in_login_is_not_accepted() throws Exception {
        String json = """
            {
                "email": "",
                "password": "password123"
            }
            """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

}
