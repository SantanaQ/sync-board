package com.backend.project_member.application;

import com.backend.TestDataFactory;
import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.ResourceAlreadyExistsException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.exception.UnauthorizedOperationException;
import com.backend.project.domain.Project;
import com.backend.project.infrastructure.ProjectRepository;
import com.backend.project_member.api.AddMemberRequest;
import com.backend.project_member.api.ProjectMemberResponse;
import com.backend.project_member.api.UpdateMemberRequest;
import com.backend.project_member.domain.MemberRole;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectMemberId;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.project_member.infrastructure.ProjectMemberRepository;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import com.backend.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectMemberServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository memberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectMemberService projectMemberService;

    @Test
    void getMembers_returns_all_project_members_if_user_is_member() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        User ownerUser = TestDataFactory.user(
                UUID.randomUUID(),
                "owner@email.com",
                "owner",
                "password"
        );


        ProjectMember member = new ProjectMember(project, user, MemberRole.MEMBER);
        ProjectMember owner = new ProjectMember(project, ownerUser, MemberRole.OWNER);

        List<ProjectMember> projectMembers = List.of(owner, member);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(member);

        when(memberRepository.findAllByProjectId(projectId))
                .thenReturn(projectMembers);

        List<ProjectMemberResponse> result = projectMemberService.getMembers(projectId);

        assertThat(result.size()).isEqualTo(2);

        verify(memberRepository).findAllByProjectId(projectId);
    }

    @Test
    void getMembers_throws_access_denied_if_user_is_not_member_of_project() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> projectMemberService.getMembers(projectId))
                .isInstanceOf(AccessDeniedException.class);

        verify(projectAuthorizationService).requireMembership(projectId, user);
        verifyNoInteractions(memberRepository);
    }

    @Test
    void getMember_throws_access_denied_if_user_is_not_member_of_project() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> projectMemberService.getMember(projectId, userId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(memberRepository);
    }

    @Test
    void getMember_throws_resource_not_found_if_requested_member_is_not_member_of_project() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userMember = new ProjectMember(project, user, MemberRole.MEMBER);

        UUID memberId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        // requesting user is project member
        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(userMember);

        // requested member is not project member
        when(memberRepository.findById(new ProjectMemberId(projectId, memberId)))
                .thenThrow(ResourceNotFoundException.class);

        assertThatThrownBy(() -> projectMemberService.getMember(projectId, memberId))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void getMember_returns_member_if_requesting_user_is_member_of_project_and_requested_member_is_present() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userMember = new ProjectMember(project, user, MemberRole.MEMBER);

        UUID memberId = UUID.randomUUID();
        User member = TestDataFactory.user(
                memberId,
                "test1@email.com",
                "member",
                "password"
        );
        ProjectMember requestMember = new ProjectMember(project, member, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(userMember);

        when(memberRepository.findById(new ProjectMemberId(projectId, memberId)))
                .thenReturn(Optional.of(requestMember));

        ProjectMemberResponse result = projectMemberService.getMember(projectId, memberId);

        assertThat(result).isNotNull();
        assertThat(result.user().displayName()).isEqualTo(member.displayName());

    }

    @Test
    void addMember_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_ADD)
        ).thenThrow(AccessDeniedException.class);

        AddMemberRequest request = new AddMemberRequest(
                UUID.randomUUID(),
                MemberRole.MEMBER
        );

        assertThatThrownBy(() -> projectMemberService.addMember(projectId, request))
                .isInstanceOf(AccessDeniedException.class);

        verify(projectAuthorizationService)
                .requirePermission(projectId, user, ProjectPermission.MEMBER_ADD);

    }

    @Test
    void addMember_throws_resource_not_found_if_project_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_ADD)
        ).thenReturn(userOwner);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        AddMemberRequest request = new AddMemberRequest(
                UUID.randomUUID(),
                MemberRole.MEMBER
        );

        assertThatThrownBy(() -> projectMemberService.addMember(projectId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository).findById(projectId);
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void addMember_throws_resource_not_found_if_added_member_is_not_a_user() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_ADD)
        ).thenReturn(userOwner);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        UUID memberId = UUID.randomUUID();

        when(userRepository.findById(memberId))
                .thenReturn(Optional.empty());

        AddMemberRequest request = new AddMemberRequest(
                memberId,
                MemberRole.MEMBER
        );

        assertThatThrownBy(() -> projectMemberService.addMember(projectId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository).findById(memberId);
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void addMember_throws_resource_already_exists_if_added_member_is_already_project_member() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        UUID memberId = UUID.randomUUID();
        User member = TestDataFactory.user(
                memberId,
                "member@email.com",
                "member",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_ADD)
        ).thenReturn(userOwner);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(userRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(memberRepository.existsById(new ProjectMemberId(projectId, memberId)))
                .thenReturn(true);

        AddMemberRequest request = new AddMemberRequest(
                memberId,
                MemberRole.MEMBER
        );

        assertThatThrownBy(() -> projectMemberService.addMember(projectId, request))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(memberRepository).existsById(new ProjectMemberId(projectId, memberId));
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void addMember_throws_unauthorized_operation_if_added_member_is_assigned_role_owner() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        UUID memberId = UUID.randomUUID();
        User member = TestDataFactory.user(
                memberId,
                "member@email.com",
                "member",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_ADD)
        ).thenReturn(userOwner);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(userRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(memberRepository.existsById(new ProjectMemberId(projectId, memberId)))
                .thenReturn(false);

        AddMemberRequest request = new AddMemberRequest(
                memberId,
                MemberRole.OWNER
        );

        assertThatThrownBy(() -> projectMemberService.addMember(projectId, request))
                .isInstanceOf(UnauthorizedOperationException.class);

        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void addMember_adds_new_member_to_project() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        UUID memberId = UUID.randomUUID();
        User requestMember = TestDataFactory.user(
                memberId,
                "member@email.com",
                "member",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_ADD)
        ).thenReturn(userOwner);

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(userRepository.findById(memberId))
                .thenReturn(Optional.of(requestMember));

        when(memberRepository.existsById(new ProjectMemberId(projectId, memberId)))
                .thenReturn(false);

        when(memberRepository.save(any(ProjectMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AddMemberRequest request = new AddMemberRequest(
                memberId,
                MemberRole.MEMBER
        );

        projectMemberService.addMember(projectId, request);

        verify(memberRepository).save(any(ProjectMember.class));
    }

    @Test
    void removeMember_throws_access_denied_if_requesting_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_REMOVE)
        ).thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> projectMemberService.removeMember(projectId, userId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(memberRepository);
        verify(projectAuthorizationService)
                .requirePermission(projectId, user, ProjectPermission.MEMBER_REMOVE);
    }

    @Test
    void removeMember_throws_resource_not_found_if_removed_user_is_not_member_of_project() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_REMOVE)
        ).thenReturn(userOwner);

        when(memberRepository.findById(new ProjectMemberId(projectId, userId)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectMemberService.removeMember(projectId, userId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(memberRepository).findById(new ProjectMemberId(projectId, userId));
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void removeMember_throws_unauthorized_operation_if_project_owner_is_being_removed() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_REMOVE)
        ).thenReturn(userOwner);

        when(memberRepository.findById(new ProjectMemberId(projectId, userId)))
                .thenReturn(Optional.of(userOwner));

        assertThatThrownBy(() -> projectMemberService.removeMember(projectId, userId))
                .isInstanceOf(UnauthorizedOperationException.class);

        verifyNoMoreInteractions(memberRepository);

    }

    @Test
    void removeMember_removes_project_member() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);

        UUID removeUserId = UUID.randomUUID();
        User toRemoveUser = TestDataFactory.user(
                removeUserId,
                "member@email.com",
                "member",
                "password"
        );

        ProjectMember toRemoveMember = new ProjectMember(project, toRemoveUser, MemberRole.MEMBER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_REMOVE)
        ).thenReturn(userOwner);

        when(memberRepository.findById(new ProjectMemberId(projectId, removeUserId)))
                .thenReturn(Optional.of(toRemoveMember));

        projectMemberService.removeMember(projectId, removeUserId);

        verify(memberRepository).delete(toRemoveMember);
    }

    @Test
    void updateMember_throws_access_denied_if_requesting_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_UPDATE)
        ).thenThrow(AccessDeniedException.class);

        UpdateMemberRequest request = new UpdateMemberRequest(MemberRole.MEMBER);

        assertThatThrownBy(() -> projectMemberService.updateMember(projectId, userId, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateMember_throws_resource_not_found_if_requested_project_member_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);
        UUID updateUserId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_UPDATE)
        ).thenReturn(userOwner);

        when(memberRepository.findById(new ProjectMemberId(projectId, updateUserId)))
                .thenReturn(Optional.empty());


        UpdateMemberRequest request = new UpdateMemberRequest(MemberRole.MEMBER);
        assertThatThrownBy(() ->
                projectMemberService.updateMember(projectId, updateUserId, request)
        ).isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void updateMember_throws_unauthorized_operation_if_updated_role_is_owner() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);
        UUID updateUserId = UUID.randomUUID();

        User updateUser = TestDataFactory.user(
                updateUserId,
                "member@email.com",
                "member",
                "password"
        );

        ProjectMember updateMember = new ProjectMember(project, updateUser, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_UPDATE)
        ).thenReturn(userOwner);

        when(memberRepository.findById(new ProjectMemberId(projectId, updateUserId)))
                .thenReturn(Optional.of(updateMember));


        UpdateMemberRequest request = new UpdateMemberRequest(MemberRole.OWNER);
        assertThatThrownBy(() ->
                projectMemberService.updateMember(projectId, updateUserId, request)
        ).isInstanceOf(UnauthorizedOperationException.class);

        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void updateMember_updates_member_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "test@email.com",
                "user",
                "password"
        );

        Project project = TestDataFactory.project(
                projectId,
                "project",
                "description"
        );

        ProjectMember userOwner = new ProjectMember(project, user, MemberRole.OWNER);
        UUID updateUserId = UUID.randomUUID();

        User updateUser = TestDataFactory.user(
                updateUserId,
                "member@email.com",
                "member",
                "password"
        );

        ProjectMember updateMember = new ProjectMember(project, updateUser, MemberRole.MEMBER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.MEMBER_UPDATE)
        ).thenReturn(userOwner);

        when(memberRepository.findById(new ProjectMemberId(projectId, updateUserId)))
                .thenReturn(Optional.of(updateMember));

        when(memberRepository.save(updateMember))
                .thenReturn(updateMember);


        UpdateMemberRequest request = new UpdateMemberRequest(MemberRole.ADMIN);

        ProjectMemberResponse response
                = projectMemberService.updateMember(projectId, updateUserId, request);

        assertThat(response.role()).isEqualTo(MemberRole.ADMIN);

        verify(memberRepository).save(updateMember);
    }


}


