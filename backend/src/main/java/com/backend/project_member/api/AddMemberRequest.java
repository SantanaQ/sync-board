package com.backend.project_member.api;

import com.backend.project_member.domain.MemberRole;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AddMemberRequest(
        @NotBlank
        @org.hibernate.validator.constraints.UUID
        UUID userId,

        @NotBlank
        MemberRole role
) {
}
