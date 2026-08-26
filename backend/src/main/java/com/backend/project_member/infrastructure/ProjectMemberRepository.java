package com.backend.project_member.infrastructure;

import com.backend.project_member.domain.MemberRole;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectMemberId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    Optional<ProjectMember> findById(ProjectMemberId projectMemberId);

    @EntityGraph(attributePaths = "user")
    Optional<ProjectMember> findByIdProjectIdAndRole(
            UUID projectId,
            MemberRole role
    );

    List<ProjectMember> findAllByProjectId(UUID projectId);


    @Query("""
        SELECT pm
        FROM ProjectMember pm
        JOIN FETCH pm.user
        WHERE pm.project.id = :projectId
        """)
    List<ProjectMember> findAllByProjectIdWithUser(UUID projectId);
}
