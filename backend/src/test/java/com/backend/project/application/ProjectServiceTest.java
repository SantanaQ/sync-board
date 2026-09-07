package com.backend.project.application;

import com.backend.TestDataFactory;
import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project.api.CreateProjectRequest;
import com.backend.project.api.ProjectResponse;
import com.backend.project.api.UpdateProjectRequest;
import com.backend.project.domain.Project;
import com.backend.project.infrastructure.ProjectRepository;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.MemberRole;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.project_member.infrastructure.ProjectMemberRepository;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository memberRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void getProject_returns_project_for_member() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User ownerUser = TestDataFactory.user(
                ownerId,
                "owner@email.com",
                "owner",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "projectName",
                "projectDescription"
        );

        ProjectMember member = TestDataFactory.projectMember(projectId, userId, MemberRole.MEMBER);
        ProjectMember owner = TestDataFactory.projectMember(projectId, ownerId, MemberRole.MEMBER);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(projectAuthorizationService.requirePermission(
                projectId,
                ProjectPermission.PROJECT_VIEW)
        ).thenReturn(member);

        when(memberRepository.findByIdProjectIdAndRole(
                projectId,
                MemberRole.OWNER
        ))
                .thenReturn(Optional.of(owner));

        ProjectResponse result =
                projectService.getProject(projectId);

        assertThat(result.id())
                .isEqualTo(project.id());

        assertThat(result.name())
                .isEqualTo(project.name());

        assertThat(result.description())
                .isEqualTo(project.description());

        assertThat(result.currentUserRole())
                .isEqualTo(MemberRole.MEMBER);

        assertThat(result.owner().id())
                .isEqualTo(ownerUser.id());    }


    @Test
    void getProject_throws_resource_not_found_when_project_does_not_exist() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProject(projectId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository).findById(projectId);
    }

    @Test
    void getProject_throws_access_denied_if_user_is_not_member() {
        UUID projectId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.PROJECT_VIEW))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> projectService.getProject(projectId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getProject_throws_illegal_state_if_project_has_no_owner() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Project project = TestDataFactory.project(
                projectId,
                "projectName",
                "projectDescription"
        );

        ProjectMember member = TestDataFactory.projectMember(projectId, userId, MemberRole.MEMBER);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.PROJECT_VIEW))
                .thenReturn(member);

        when(memberRepository.findByIdProjectIdAndRole(projectId, MemberRole.OWNER))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProject(projectId))
                .isInstanceOf(IllegalStateException.class);

        verify(memberRepository).findByIdProjectIdAndRole(projectId, MemberRole.OWNER);
    }

    @Test
    void createProject_creates_project_with_user_as_owner() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        Project project = TestDataFactory.project(
                projectId,
                "projectName",
                "projectDescription"
        );

        ProjectMember currentUserOwner
                = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        when(memberRepository.save(any(ProjectMember.class))).thenReturn(currentUserOwner);

        CreateProjectRequest request = new CreateProjectRequest(
                project.name(),
                project.description()
        );

        ProjectResponse response = projectService.createProject(request);

        assertThat(response.id()).isEqualTo(project.id());

        assertThat(response.name()).isEqualTo(project.name());

        assertThat(response.description()).isEqualTo(project.description());

        assertThat(response.currentUserRole()).isEqualTo(MemberRole.OWNER);

        assertThat(response.owner().id()).isEqualTo(userId);

        verify(projectRepository).save(any(Project.class));
        verify(memberRepository).save(any(ProjectMember.class));
    }

    @Test
    void updateProject_updates_project_when_user_has_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Project project = TestDataFactory.project(
                projectId,
                "projectName",
                "projectDescription"
        );

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);


        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(projectAuthorizationService
                .requirePermission(projectId, ProjectPermission.PROJECT_UPDATE)
        ).thenReturn(owner);

        when(memberRepository.findByIdProjectIdAndRole(
                projectId,
                MemberRole.OWNER
        ))
                .thenReturn(Optional.of(owner));

        UpdateProjectRequest request =
                new UpdateProjectRequest(
                        "updatedName",
                        "updatedDescription"
                );

        ProjectResponse response =
                projectService.updateProject(projectId, request);

        assertThat(response.id())
                .isEqualTo(project.id());

        assertThat(response.name())
                .isEqualTo(request.name());

        assertThat(response.description())
                .isEqualTo(request.description());

        assertThat(response.currentUserRole())
                .isEqualTo(MemberRole.OWNER);

        assertThat(response.owner().id())
                .isEqualTo(userId);

        assertThat(project.name())
                .isEqualTo(request.name());

        assertThat(project.description())
                .isEqualTo(request.description());
    }

    @Test
    void updateProject_throws_access_denied_when_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        Project project = TestDataFactory.project(
                projectId,
                "projectName",
                "projectDescription"
        );

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(projectAuthorizationService
                .requirePermission(projectId, ProjectPermission.PROJECT_UPDATE)
        ).thenThrow(AccessDeniedException.class);

        UpdateProjectRequest request =
                new UpdateProjectRequest(
                        "updatedName",
                        "updatedDescription"
                );

        assertThatThrownBy(() -> projectService.updateProject(projectId, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateProject_throws_resource_not_found_when_project_does_not_exist() {
        UUID projectId = UUID.randomUUID();

        UpdateProjectRequest request =
                new UpdateProjectRequest(
                        "updatedName",
                        "updatedDescription"
                );

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateProject(projectId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository).findById(projectId);
    }

    @Test
    void deleteProject_deletes_project_when_user_has_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Project project = TestDataFactory.project(
                projectId,
                "projectName",
                "projectDescription"
        );

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        when(projectAuthorizationService
                .requirePermission(projectId, ProjectPermission.PROJECT_DELETE)
        ).thenReturn(owner);

        projectService.deleteProject(projectId);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_throws_access_denied_when_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();

        when(projectAuthorizationService
                .requirePermission(projectId, ProjectPermission.PROJECT_DELETE)
        ).thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> projectService.deleteProject(projectId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void deleteProject_throws_resource_not_found_when_project_does_not_exist() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.deleteProject(projectId))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(projectRepository);
    }



}
