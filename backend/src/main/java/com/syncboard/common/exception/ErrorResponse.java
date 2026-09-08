package com.syncboard.common.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        Instant timestamp,
        Map<String, String> errors
) {
}
