package com.syncboard.card.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CardResponse(
        UUID id,
        UUID columnId,
        String title,
        String description,
        BigDecimal position,
        Instant createdAt,
        Instant updatedAt
) {
}
