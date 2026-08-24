package com.backend.project_members.domain;

import com.backend.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Table(name = "project_members")
@Entity
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @NotNull
    private MemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;


    protected ProjectMember() {
        // JPA
    }

    public ProjectMember(ProjectMemberId id, MemberRole role) {
        this.id = id;
        this.role = role;
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

}
