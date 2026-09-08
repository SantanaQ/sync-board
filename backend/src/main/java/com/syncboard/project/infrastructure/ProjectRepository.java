package com.syncboard.project.infrastructure;

import com.syncboard.project.api.ProjectListResponse;
import com.syncboard.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("""
        select new com.syncboard.project.api.ProjectListResponse(
            p.id,
            p.name,
            p.description,
            p.createdAt,
            p.updatedAt,
            pm.role
        )
        from Project p
        join ProjectMember pm
            on pm.id.projectId = p.id
        where pm.id.userId = :userId
        """)
    List<ProjectListResponse> findProjectsForUser(
            @Param("userId") UUID userId
    );

}
