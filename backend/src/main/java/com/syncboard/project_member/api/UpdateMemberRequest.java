package com.syncboard.project_member.api;

import com.syncboard.project_member.domain.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRequest(
        @NotNull
        MemberRole role
) {
}
