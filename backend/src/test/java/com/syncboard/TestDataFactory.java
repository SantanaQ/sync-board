package com.syncboard;

import com.syncboard.board.domain.Board;
import com.syncboard.board_column.api.CreateBoardColumnRequest;
import com.syncboard.board_column.api.UpdateBoardColumnRequest;
import com.syncboard.board_column.domain.BoardColumn;
import com.syncboard.card.api.CreateCardRequest;
import com.syncboard.card.api.MoveCardRequest;
import com.syncboard.card.api.UpdateCardRequest;
import com.syncboard.card.domain.Card;
import com.syncboard.project.domain.Project;
import com.syncboard.project_member.domain.MemberRole;
import com.syncboard.project_member.domain.ProjectMember;
import com.syncboard.project_member.domain.ProjectMemberId;
import com.syncboard.user.domain.User;
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

    public static BoardColumn column(UUID id, UUID boardId, UUID projectId) {
        Board board = board(boardId, projectId, "board");
        return column(id, board, "column", BigDecimal.ZERO);
    }

    public static CreateBoardColumnRequest createBoardColumnRequest() {
        return new CreateBoardColumnRequest("column");
    }

    public static UpdateBoardColumnRequest updateBoardColumnRequest() {
        return new UpdateBoardColumnRequest("updatedColumn");
    }

    public static Card card(UUID cardId, UUID columnId) {
        BoardColumn column = column(
                columnId,
                null,
                "col",
                BigDecimal.ZERO
        );
        Card card = new Card(
                column,
                "card",
                "description",
                BigDecimal.valueOf(1000)
        );
        ReflectionTestUtils.setField(card, "id", cardId);
        return card;
    }

    public static Card card(UUID cardId, UUID columnID, BigDecimal position) {
        Card card = card(cardId, columnID);
        card.setPosition(position);
        return card;
    }

    public static CreateCardRequest createCardRequest() {
        return new CreateCardRequest("cardTitle", "cardDescription");
    }

    public static UpdateCardRequest updateCardRequest() {
        return new UpdateCardRequest("updatedCard", "updatedDescription", 1);
    }

    public static MoveCardRequest moveCardRequest(UUID newColumnId) {
        return new MoveCardRequest(newColumnId);
    }


}
