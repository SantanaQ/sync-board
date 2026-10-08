package com.syncboard.board.api;

import java.time.Instant;
import java.util.UUID;

public record BoardResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
