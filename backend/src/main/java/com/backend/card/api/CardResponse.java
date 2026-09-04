package com.backend.card.api;

import java.time.Instant;
import java.util.UUID;

public record CardResponse(
        UUID id,
        UUID columnId,
        String title,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
