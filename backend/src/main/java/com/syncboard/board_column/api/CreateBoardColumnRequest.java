package com.syncboard.board_column.api;

import jakarta.validation.constraints.NotBlank;

public record CreateBoardColumnRequest(
        @NotBlank
        String name
) {
}
