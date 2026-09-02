package com.backend.board_column.api;

import jakarta.validation.constraints.NotBlank;

public record UpdateBoardColumnRequest(
        @NotBlank
        String name
) {
}
