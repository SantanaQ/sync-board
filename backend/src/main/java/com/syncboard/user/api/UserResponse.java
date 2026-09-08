package com.syncboard.user.api;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String displayName,
        String email
) {
}
