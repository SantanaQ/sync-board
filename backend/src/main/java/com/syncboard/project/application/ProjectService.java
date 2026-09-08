package com.syncboard.project.application;

import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.project.api.CreateProjectRequest;
import com.syncboard.project.api.ProjectListResponse;
import com.syncboard.project.api.ProjectResponse;
import com.syncboard.project.api.UpdateProjectRequest;
import com.syncboard.project.domain.Project;
import com.syncboard.project.infrastructure.ProjectRepository;
import com.syncboard.project_member.application.ProjectAuthorizationService;
import com.syncboard.project_member.domain.MemberRole;
import com.syncboard.project_member.domain.ProjectMember;
import com.syncboard.project_member.domain.ProjectPermission;
import com.syncboard.project_member.infrastructure.ProjectMemberRepository;
import com.syncboard.user.api.UserResponse;
import com.syncboard.user.application.CurrentUserService;
import com.syncboard.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectAuthorizationService projectAuthorizationService;

    public ProjectService(
            ProjectRepository projectRepository,
            CurrentUserService currentUserService,
            ProjectMemberRepository projectMemberRepository,
            ProjectAuthorizationService projectAuthorizationService
    ) {
        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
        this.projectMemberRepository = projectMemberRepository;
        this.projectAuthorizationService = projectAuthorizationService;

    }

    public ProjectResponse getProject(UUID id) {
        ProjectMember currentUserMember
                = projectAuthorizationService.requirePermission(id, ProjectPermission.PROJECT_VIEW);

        Project project = requirePresence(id);

        return toResponse(project, currentUserMember);
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User currentUser = currentUserService.get();

        Project project = new Project(request.name(), request.description());

        projectRepository.save(project);

        ProjectMember owner = new ProjectMember(
                project,
                currentUser,
                MemberRole.OWNER
        );

        projectMemberRepository.save(owner);

        return new ProjectResponse(
                project.id(),
                project.name(),
                project.description(),
                project.createdAt(),
                project.updatedAt(),
                new UserResponse(
                        currentUser.id(),
                        currentUser.displayName(),
                        currentUser.email()
                ),
                MemberRole.OWNER
        );
    }

    public List<ProjectListResponse> getProjects() {
        User currentUser = currentUserService.get();

        return projectRepository.findProjectsForUser(
                currentUser.id()
        );
    }

    @Transactional
    public void deleteProject(UUID id) {
        projectAuthorizationService.requirePermission(id, ProjectPermission.PROJECT_DELETE);

        Project project = requirePresence(id);

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponse updateProject(UUID id, UpdateProjectRequest request) {
        Project project = requirePresence(id);

        ProjectMember membership = projectAuthorizationService.requirePermission(
                id,
                ProjectPermission.PROJECT_UPDATE
        );

        project.setName(request.name());
        project.setDescription(request.description());
        project.setUpdatedAt(Instant.now());

        return toResponse(project, membership);
    }

    private ProjectResponse toResponse(
            Project project,
            ProjectMember currentUserMembership
    ) {
        ProjectMember ownerMembership = projectMemberRepository
                .findByIdProjectIdAndRole(
                project.id(),
                MemberRole.OWNER
        )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Project has no owner."
                        )
                );

        User owner = ownerMembership.user();

        return new ProjectResponse(
                project.id(),
                project.name(),
                project.description(),
                project.createdAt(),
                project.updatedAt(),
                new UserResponse(
                        owner.id(),
                        owner.displayName(),
                        owner.email()
                ),
                currentUserMembership.role()
        );
    }

    private Project requirePresence(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project with id " + id + " not found."
                        )
                );
    }

}
