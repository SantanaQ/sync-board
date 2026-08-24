package com.backend.project.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        String description
) {
}
