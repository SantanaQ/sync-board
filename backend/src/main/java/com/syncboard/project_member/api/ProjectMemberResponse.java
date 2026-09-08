package com.syncboard.project_member.api;

import com.syncboard.project_member.domain.MemberRole;
import com.syncboard.user.api.UserResponse;

public record ProjectMemberResponse(
        UserResponse user,
        MemberRole role
) {
}
