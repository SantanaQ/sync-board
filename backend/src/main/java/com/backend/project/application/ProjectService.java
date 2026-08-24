package com.backend.project.application;

import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project.api.CreateProjectRequest;
import com.backend.project.api.ProjectListResponse;
import com.backend.project.api.ProjectResponse;
import com.backend.project.domain.Project;
import com.backend.project.infrastructure.ProjectRepository;
import com.backend.project_members.domain.MemberRole;
import com.backend.project_members.domain.ProjectMember;
import com.backend.project_members.domain.ProjectMemberId;
import com.backend.project_members.infrastructure.ProjectMemberRepository;
import com.backend.user.api.UserResponse;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final CurrentUserService currentUserService;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository,
                          CurrentUserService currentUserService,
                          ProjectMemberRepository projectMemberRepository
                          ) {
        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
        this.projectMemberRepository = projectMemberRepository;
    }

    public ProjectResponse getProject(UUID id) {
        User currentUser = currentUserService.get();

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project with id " + id + " not found"
                        )
                );

        ProjectMember currentUserMember = projectMemberRepository
                .findById(new ProjectMemberId(id, currentUser.id()))
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You do not have access to this project"
                        )
                );

        ProjectMember ownerMembership = projectMemberRepository
                .findByIdProjectIdAndRole(
                        id,
                        MemberRole.OWNER
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Project has no owner"
                        )
                );

        return new ProjectResponse(
                project.id(),
                project.name(),
                project.description(),
                project.createdAt(),
                project.updatedAt(),
                new UserResponse(
                        ownerMembership.user().id(),
                        ownerMembership.user().displayName(),
                        ownerMembership.user().email()
                ),
                currentUserMember.role()

        );
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User currentUser = currentUserService.get();

        Project project = new Project(request.name(), request.description());

        Project saved = projectRepository.save(project);

        ProjectMember owner = new ProjectMember(
                new ProjectMemberId(saved.id(), currentUser.id()),
                MemberRole.OWNER
        );

        projectMemberRepository.save(owner);

        return new ProjectResponse(
                saved.id(),
                saved.name(),
                saved.description(),
                saved.createdAt(),
                saved.updatedAt(),
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
    public boolean deleteProject(UUID id) {
        User currentUser = currentUserService.get();

        // TODO: check permissions

        if(projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }



}
