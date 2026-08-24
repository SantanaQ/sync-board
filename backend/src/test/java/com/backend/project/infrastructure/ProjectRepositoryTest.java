package com.backend.project.infrastructure;

import com.backend.RepositoryTestConfig;
import com.backend.project.domain.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProjectRepositoryTest extends RepositoryTestConfig {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void saves_project_and_finds_that_project_by_id() {
        Project project = new Project(
                "projectName",
                "projectDescription"
        );


        Project saved = projectRepository.save(project);

        Optional<Project> result = projectRepository.findById(saved.id());

        assertThat(result)
                .isPresent();

        assertThat(result.get().name())
                .isEqualTo(project.name());

        assertThat(result.get().description())
                .isEqualTo(project.description());
    }

    @Test
    void does_not_allow_saving_null_project_name() {
        assertThatThrownBy(() ->
                projectRepository.saveAndFlush(
                        new Project(
                                null,
                                "projectDescription"
                        )
                )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void project_is_saved_with_timestamps() {
        Instant before = Instant.now();

        Project project = new Project(
                "projectName",
                "projectDescription"
        );

        Project saved = projectRepository.save(project);

        Optional<Project> result = projectRepository.findById(saved.id());

        Instant after = Instant.now();

        assertThat(result)
                .isPresent();

        assertThat(result.get().createdAt())
                .isBetween(before, after);

        assertThat(result.get().updatedAt())
                .isBetween(before, after);
    }


}
