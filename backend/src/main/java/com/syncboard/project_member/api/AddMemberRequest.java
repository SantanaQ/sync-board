package com.syncboard.project_member.api;

import com.syncboard.project_member.domain.MemberRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(

        @NotNull
        UUID userId,

        @NotNull
        MemberRole role
) {
}
