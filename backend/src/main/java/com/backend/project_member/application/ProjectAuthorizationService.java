package com.backend.project_member.application;

import com.backend.common.exception.AccessDeniedException;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectMemberId;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.project_member.infrastructure.ProjectMemberRepository;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectAuthorizationService {

    private final CurrentUserService currentUserService;
    private final ProjectMemberRepository memberRepository;

    public ProjectAuthorizationService(
            CurrentUserService currentUserService,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.currentUserService = currentUserService;
        this.memberRepository = projectMemberRepository;
    }

    private ProjectMember requireMembership(
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
            ProjectPermission permission
    ) {

        User user = currentUserService.get();

        ProjectMember membership = requireMembership(projectId, user);

        if (!membership.hasPermission(permission)) {
            throw new AccessDeniedException(
                    "You do not have permission to perform this action."
            );
        }

        return membership;
    }

}
