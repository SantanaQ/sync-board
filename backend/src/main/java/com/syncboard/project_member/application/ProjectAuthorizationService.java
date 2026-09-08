package com.syncboard.project_member.application;

import com.syncboard.common.exception.AccessDeniedException;
import com.syncboard.project_member.domain.ProjectMember;
import com.syncboard.project_member.domain.ProjectMemberId;
import com.syncboard.project_member.domain.ProjectPermission;
import com.syncboard.project_member.infrastructure.ProjectMemberRepository;
import com.syncboard.user.application.CurrentUserService;
import com.syncboard.user.domain.User;
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
