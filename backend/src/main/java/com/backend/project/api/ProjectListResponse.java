package com.backend.project.api;

import com.backend.project_member.domain.MemberRole;

import java.time.Instant;
import java.util.UUID;

public record ProjectListResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt,
        MemberRole currentUserRole
) {
}
