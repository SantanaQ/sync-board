package com.backend.project_member.application;

import com.backend.common.exception.AccessDeniedException;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectMemberId;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.project_member.infrastructure.ProjectMemberRepository;
import com.backend.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectAuthorizationService {

    private final ProjectMemberRepository memberRepository;

    public ProjectAuthorizationService(ProjectMemberRepository projectMemberRepository) {
        this.memberRepository = projectMemberRepository;
    }

    public ProjectMember requireMembership(
            UUID projectId,
            User user
    ) {
        return memberRepository
                .findById(
                        new ProjectMemberId(
                                projectId,
                                user.id()
                        )
                )
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You do not have access to this project."
                        )
                );
    }

    public ProjectMember requirePermission(
            UUID projectId,
            User user,
            ProjectPermission permission
    ) {
        ProjectMember membership =
                requireMembership(projectId, user);

        if (!membership.hasPermission(permission)) {
            throw new AccessDeniedException(
                    "You do not have permission to perform this action."
            );
        }

        return membership;
    }

}
