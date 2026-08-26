package com.backend.board.api;

import java.time.Instant;

public record BoardResponse(
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
