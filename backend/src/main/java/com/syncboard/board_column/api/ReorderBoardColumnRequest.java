package com.syncboard.board_column.api;

import java.util.UUID;

public record ReorderBoardColumnRequest (
        @org.hibernate.validator.constraints.UUID
        UUID beforeColumnId,

        @org.hibernate.validator.constraints.UUID
        UUID afterColumnId
){
}
