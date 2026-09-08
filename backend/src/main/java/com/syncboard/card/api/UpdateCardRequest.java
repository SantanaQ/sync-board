package com.syncboard.card.api;

import jakarta.validation.constraints.NotBlank;

public record UpdateCardRequest(
        @NotBlank
        String title,

        String description,

        @NotBlank
        long version
) {
}
