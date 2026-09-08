package com.syncboard.project_member.api;

import com.syncboard.project_member.domain.MemberRole;
import jakarta.validation.constraints.NotBlank;

public record UpdateMemberRequest(
        @NotBlank
        MemberRole role
) {
}
