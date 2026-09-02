package com.backend.board_column.api;

import java.util.UUID;

public record ReorderBoardColumnRequest (
        UUID beforeColumnId,

        UUID afterColumnId
){
}
