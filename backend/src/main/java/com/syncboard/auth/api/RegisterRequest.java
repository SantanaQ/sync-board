package com.syncboard.auth.api;

import com.syncboard.common.validation.PasswordConfirmation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordConfirmation
public record RegisterRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        String passwordConfirmation
) {
}
