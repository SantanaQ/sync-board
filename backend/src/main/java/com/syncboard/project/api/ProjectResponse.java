package com.syncboard.project.api;

import com.syncboard.project_member.domain.MemberRole;
import com.syncboard.user.api.UserResponse;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        UserResponse owner,
        MemberRole currentUserRole
) {
}
