package com.backend.board_column.api;

import java.math.BigDecimal;
import java.util.UUID;

public record BoardColumnResponse(
        UUID id,
        String name,
        BigDecimal position
) {
}
