package com.backend.project_members.infrastructure;

import com.backend.project_members.domain.MemberRole;
import com.backend.project_members.domain.ProjectMember;
import com.backend.project_members.domain.ProjectMemberId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    Optional<ProjectMember> findById(ProjectMemberId projectMemberId);

    @EntityGraph(attributePaths = "user")
    Optional<ProjectMember> findByIdProjectIdAndRole(
            UUID projectId,
            MemberRole role
    );
}
