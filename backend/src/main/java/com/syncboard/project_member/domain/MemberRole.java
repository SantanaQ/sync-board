package com.syncboard.project_member.domain;

import java.util.Set;

import static com.syncboard.project_member.domain.ProjectPermission.*;

public enum MemberRole {
    OWNER(
            Set.of(
                    PROJECT_UPDATE,
                    PROJECT_DELETE,
                    PROJECT_VIEW,

                    BOARD_CREATE,
                    BOARD_UPDATE,
                    BOARD_DELETE,
                    BOARD_VIEW,

                    MEMBER_ADD,
                    MEMBER_REMOVE,
                    MEMBER_UPDATE,
                    MEMBER_VIEW,

                    COLUMN_CREATE,
                    COLUMN_DELETE,
                    COLUMN_UPDATE,
                    COLUMN_VIEW,

                    CARD_CREATE,
                    CARD_DELETE,
                    CARD_UPDATE,
                    CARD_VIEW
            )
    ),
    ADMIN(
            Set.of(
                    PROJECT_UPDATE,
                    PROJECT_VIEW,

                    BOARD_CREATE,
                    BOARD_UPDATE,
                    BOARD_DELETE,
                    BOARD_VIEW,

                    MEMBER_ADD,
                    MEMBER_REMOVE,
                    MEMBER_UPDATE,
                    MEMBER_VIEW,

                    COLUMN_CREATE,
                    COLUMN_DELETE,
                    COLUMN_UPDATE,
                    COLUMN_VIEW,

                    CARD_CREATE,
                    CARD_DELETE,
                    CARD_UPDATE,
                    CARD_VIEW
            )
    ),
    MEMBER(
            Set.of(
                    PROJECT_VIEW,

                    BOARD_VIEW,

                    MEMBER_VIEW,

                    COLUMN_VIEW,

                    CARD_CREATE,
                    CARD_DELETE,
                    CARD_UPDATE,
                    CARD_VIEW
            )
    ),
    VIEWER(
            Set.of(
                    PROJECT_VIEW,

                    BOARD_VIEW,

                    MEMBER_VIEW,

                    COLUMN_VIEW,

                    CARD_VIEW
            )
    );

    private final Set<ProjectPermission> permissions;

    MemberRole(Set<ProjectPermission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(ProjectPermission permission) {
        return permissions.contains(permission);
    }

}
