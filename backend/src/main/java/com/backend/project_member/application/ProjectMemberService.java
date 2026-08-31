package com.backend.project_member.application;

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
import com.backend.user.api.UserResponse;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import com.backend.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.backend.project_member.domain.ProjectPermission.MEMBER_REMOVE;
import static com.backend.project_member.domain.ProjectPermission.MEMBER_UPDATE;

@Service
public class ProjectMemberService {

    private final CurrentUserService currentUserService;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(CurrentUserService currentUserService,
                                ProjectAuthorizationService projectAuthorizationService,
                                ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository
                                ) {
        this.currentUserService = currentUserService;
        this.projectAuthorizationService = projectAuthorizationService;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectMemberResponse> getMembers(UUID projectId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

        return projectMemberRepository.findAllByProjectId(projectId)
                .stream()
                .map(projectMember -> new ProjectMemberResponse(
                        new UserResponse(
                                projectMember.user().id(),
                                projectMember.user().displayName(),
                                projectMember.user().email()),
                        projectMember.role())
                ).toList();
    }

    public ProjectMemberResponse getMember(UUID projectId, UUID userId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

        ProjectMember pm = requirePresence(projectId, userId);

        return toResponse(pm);
    }

    @Transactional
    public ProjectMemberResponse addMember(UUID projectId, AddMemberRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.MEMBER_ADD);

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project with id " + projectId + " not found."
                        )
                );

        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with id " + request.userId() + " not found."
                        )
                );

        if (projectMemberRepository.existsById(
                new ProjectMemberId(projectId, request.userId())
        )) {
            throw new ResourceAlreadyExistsException(
                    "User is already a member of this project."
            );
        }

        if(request.role() == MemberRole.OWNER) {
            throw new UnauthorizedOperationException(
                    "Cannot assign role 'owner' to another member of this project."
            );
        }

        ProjectMember pm = new ProjectMember(project, user, request.role());

        ProjectMember saved = projectMemberRepository.save(pm);

        return toResponse(saved);
    }

    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                MEMBER_REMOVE
        );

        ProjectMember toDelete = requirePresence(projectId, userId);

        if(toDelete.role() == MemberRole.OWNER) {
            throw new UnauthorizedOperationException(
                    "Cannot remove owner of this project."
            );
        }

        projectMemberRepository.delete(toDelete);
    }

    @Transactional
    public ProjectMemberResponse updateMember(UUID projectId,
                                              UUID userId,
                                              UpdateMemberRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                MEMBER_UPDATE
        );

        ProjectMember toUpdate = requirePresence(projectId, userId);

        if(request.role() == MemberRole.OWNER) {
            throw new UnauthorizedOperationException(
                    "Cannot assign role 'owner' to another member of this project."
            );
        }

        toUpdate.setRole(request.role());

        ProjectMember updated = projectMemberRepository.save(toUpdate);

        return toResponse(updated);
    }


    private ProjectMember requirePresence(UUID projectId, UUID userId) {
        return projectMemberRepository.findById(
                new ProjectMemberId(projectId, userId)
        ).orElseThrow(() -> new ResourceNotFoundException("Project member not found."));
    }

    private ProjectMemberResponse toResponse(ProjectMember member) {
        User user = member.user();

        return new ProjectMemberResponse(
                new UserResponse(
                        user.id(),
                        user.displayName(),
                        user.email()
                ),
                member.role()
        );
    }


}
