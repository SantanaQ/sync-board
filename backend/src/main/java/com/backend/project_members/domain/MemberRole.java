package com.backend.project_members.domain;

import java.util.Set;

import static com.backend.project_members.domain.ProjectPermission.*;

public enum MemberRole {
    OWNER(
            Set.of(
                    PROJECT_UPDATE,
                    PROJECT_DELETE
            )
    ),
    ADMIN(
            Set.of(
                    PROJECT_UPDATE
            )
    ),
    MEMBER(
            Set.of()
    ),
    VIEWER(
            Set.of()
    );

    private final Set<ProjectPermission> permissions;

    MemberRole(Set<ProjectPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(ProjectPermission permission) {
        return permissions.contains(permission);
    }

}
