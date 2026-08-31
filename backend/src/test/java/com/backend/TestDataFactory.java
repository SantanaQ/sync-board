package com.backend;

import com.backend.board.domain.Board;
import com.backend.project.domain.Project;
import com.backend.project_member.domain.MemberRole;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectMemberId;
import com.backend.user.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class TestDataFactory {

    public static User user(UUID id, String email, String displayName, String password) {
        User user =  new User(email,displayName, password);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static Project project(UUID id, String name, String description) {
        Project project = new Project(name,description);
        ReflectionTestUtils.setField(project, "id", id);
        return project;
    }

    public static ProjectMember projectMember(UUID projectId, UUID userId, MemberRole role) {
        Project project = project(projectId, "project", "description");
        User user = user(userId, "test@email.com", "user", "password");
        ProjectMember member = new ProjectMember(project, user, role);
        ReflectionTestUtils.setField(member, "id", new ProjectMemberId(projectId, userId));
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "project", project);
        return member;
    }

    public static Board board(UUID id, UUID projectId, String name) {
        Project project = project(projectId, "project", "description");
        Board board = new Board(name, project);
        ReflectionTestUtils.setField(board, "id", id);
        ReflectionTestUtils.setField(board, "project", project);
        return board;
    }




}
