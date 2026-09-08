package com.syncboard.project_member.application;

import com.syncboard.TestDataFactory;
import com.syncboard.common.exception.AccessDeniedException;
import com.syncboard.project_member.domain.MemberRole;
import com.syncboard.project_member.domain.ProjectMember;
import com.syncboard.project_member.domain.ProjectMemberId;
import com.syncboard.project_member.domain.ProjectPermission;
import com.syncboard.project_member.infrastructure.ProjectMemberRepository;
import com.syncboard.user.application.CurrentUserService;
import com.syncboard.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectAuthorizationServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private ProjectAuthorizationService projectAuthorizationService;

    @Test
    void requirePermission_throws_access_denied_if_user_is_not_member_of_project() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        when(currentUserService.get()).thenReturn(user);

        when(projectMemberRepository.findById(any(ProjectMemberId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectAuthorizationService.requirePermission(
                projectId,
                ProjectPermission.PROJECT_VIEW
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void requirePermission_throws_access_denied_if_user_does_not_have_permission() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember viewer = TestDataFactory.projectMember(projectId, userId, MemberRole.VIEWER);

        when(currentUserService.get()).thenReturn(user);

        when(projectMemberRepository.findById(any(ProjectMemberId.class)))
                .thenReturn(Optional.of(viewer));

        assertThatThrownBy(() -> projectAuthorizationService.requirePermission(
                projectId,
                ProjectPermission.PROJECT_DELETE
        )).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void requirePermission_returns_project_member_if_user_has_permission() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember viewer = TestDataFactory.projectMember(projectId, userId, MemberRole.VIEWER);

        when(currentUserService.get()).thenReturn(user);

        when(projectMemberRepository.findById(any(ProjectMemberId.class)))
                .thenReturn(Optional.of(viewer));

        ProjectMember member = projectAuthorizationService.requirePermission(
                projectId,
                ProjectPermission.PROJECT_VIEW);

        assertThat(member.user().id()).isEqualTo(userId);
    }


}
