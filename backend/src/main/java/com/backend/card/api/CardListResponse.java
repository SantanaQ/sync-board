package com.backend.card.api;

import java.math.BigDecimal;
import java.util.UUID;

public record CardListResponse(
        UUID id,
        String title,
        String description,
        BigDecimal position
) {
}
