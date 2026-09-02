package com.backend.project_member.domain;

import java.util.Set;

import static com.backend.project_member.domain.ProjectPermission.*;

public enum MemberRole {
    OWNER(
            Set.of(
                    PROJECT_UPDATE,
                    PROJECT_DELETE,

                    BOARD_CREATE,
                    BOARD_UPDATE,
                    BOARD_DELETE,

                    MEMBER_ADD,
                    MEMBER_REMOVE,
                    MEMBER_UPDATE,

                    COLUMN_CREATE,
                    COLUMN_DELETE,
                    COLUMN_UPDATE
            )
    ),
    ADMIN(
            Set.of(
                    PROJECT_UPDATE,

                    BOARD_CREATE,
                    BOARD_UPDATE,
                    BOARD_DELETE,

                    MEMBER_ADD,
                    MEMBER_REMOVE,
                    MEMBER_UPDATE,

                    COLUMN_CREATE,
                    COLUMN_DELETE,
                    COLUMN_UPDATE
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
