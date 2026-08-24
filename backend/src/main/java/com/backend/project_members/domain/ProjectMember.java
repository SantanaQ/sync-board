package com.backend.project_members.domain;

import com.backend.project.domain.Project;
import com.backend.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Table(name = "project_members")
@Entity
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MemberRole role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    protected ProjectMember() {
        // JPA
    }

    public ProjectMember(Project project, User user, MemberRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
        this.id = new ProjectMemberId(
                project.id(),
                user.id()
        );
    }

    public ProjectMemberId id() {
        return id;
    }

    public @NotNull MemberRole role() {
        return role;
    }

    public User user() {
        return user;
    }

    public Project project() {
        return project;
    }

    public boolean hasPermission(ProjectPermission permission) {
        return role.hasPermission(permission);
    }

}
