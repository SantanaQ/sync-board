package com.backend.project_member.api;

import com.backend.project_member.domain.MemberRole;
import com.backend.user.api.UserResponse;

public record ProjectMemberResponse(
        UserResponse user,
        MemberRole role
) {
}
