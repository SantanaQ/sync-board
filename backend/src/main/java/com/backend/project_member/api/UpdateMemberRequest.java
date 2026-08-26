package com.backend.project_member.api;

import com.backend.project_member.domain.MemberRole;
import jakarta.validation.constraints.NotBlank;

public record UpdateMemberRequest(
        @NotBlank
        MemberRole role
) {
}
