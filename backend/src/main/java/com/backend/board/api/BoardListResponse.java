package com.backend.board.api;

import java.time.Instant;

public record BoardListResponse(
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
