package com.syncboard.card.api;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MoveCardRequest(
        @NotBlank
        @org.hibernate.validator.constraints.UUID
        UUID newColumnId
) {
}
