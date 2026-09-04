package com.backend.card.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCardRequest(
        @NotBlank
        @Size(max = 255)
        String title,

        String description
) {
}
