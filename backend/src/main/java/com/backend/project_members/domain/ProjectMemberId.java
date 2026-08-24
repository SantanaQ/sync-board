package com.backend.project_members.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ProjectMemberId implements Serializable {

    @NotNull
    @Column(name = "project_id")
    private UUID projectId;

    @NotNull
    @Column(name = "user_id")
    private UUID userId;

    protected ProjectMemberId() {
        // JPA
    }

    public ProjectMemberId(UUID projectId, UUID userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public UUID projectId() {
        return projectId;
    }

    public UUID userId() {
        return userId;
    }


}
