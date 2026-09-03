package com.backend;

import com.backend.board.domain.Board;
import com.backend.board_column.api.CreateBoardColumnRequest;
import com.backend.board_column.api.ReorderBoardColumnRequest;
import com.backend.board_column.api.UpdateBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.project.domain.Project;
import com.backend.project_member.domain.MemberRole;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectMemberId;
import com.backend.user.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class TestDataFactory {

    public static User user(UUID id, String email, String displayName, String password) {
        User user =  new User(email,displayName, password);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static User user(UUID id) {
        return user(id, "user@email.com", "user", "password");
    }

    public static Project project(UUID id, String name, String description) {
        Project project = new Project(name,description);
        ReflectionTestUtils.setField(project, "id", id);
        return project;
    }

    public static Project project(UUID id) {
        return project(id, "project", "description");
    }

    public static ProjectMember projectMember(UUID projectId, UUID userId, MemberRole role) {
        Project project = project(projectId);
        User user = user(userId);
        ProjectMember member = new ProjectMember(project, user, role);
        ReflectionTestUtils.setField(member, "id", new ProjectMemberId(projectId, userId));
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "project", project);
        return member;
    }

    public static ProjectMember projectMember(Project project, User user, MemberRole role) {
        ProjectMember member = new ProjectMember(project, user, role);
        ReflectionTestUtils.setField(
                member,
                "id",
                new ProjectMemberId(project.id(), user.id())
        );
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "project", project);
        return member;
    }

    public static Board board(UUID id, UUID projectId, String name) {
        Project project = project(projectId);
        Board board = new Board(name, project);
        ReflectionTestUtils.setField(board, "id", id);
        ReflectionTestUtils.setField(board, "project", project);
        return board;
    }

    public static Board board(UUID id, Project project, String name) {
        Board board = new Board(name, project);
        ReflectionTestUtils.setField(board, "id", id);
        ReflectionTestUtils.setField(board, "project", project);
        return board;
    }

    public static BoardColumn column(UUID id, Board board, String name, BigDecimal position) {
        BoardColumn column = new BoardColumn(board, name, position);
        ReflectionTestUtils.setField(column, "id", id);
        ReflectionTestUtils.setField(column, "board", board);
        return column;
    }

    public static CreateBoardColumnRequest createBoardColumnRequest() {
        return new CreateBoardColumnRequest("board");
    }

    public static UpdateBoardColumnRequest updateBoardColumnRequest() {
        return new UpdateBoardColumnRequest("board");
    }


}
