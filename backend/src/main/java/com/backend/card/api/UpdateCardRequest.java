package com.backend.card.api;

import jakarta.validation.constraints.NotBlank;

import java.math.BigInteger;

public record UpdateCardRequest(
        @NotBlank
        String title,

        String description,

        BigInteger version
) {
}
