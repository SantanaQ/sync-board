package com.backend.board_column.application;

import com.backend.TestDataFactory;
import com.backend.board.domain.Board;
import com.backend.board.infrastructure.BoardRepository;
import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.CreateBoardColumnRequest;
import com.backend.board_column.api.UpdateBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.MemberRole;
import com.backend.project_member.domain.ProjectMember;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardColumnServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @InjectMocks
    private BoardColumnService boardColumnService;

    @Test
    void getColumns_throws_access_denied_if_user_is_not_member_of_project() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> boardColumnService.getColumns(projectId, boardId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardColumnRepository);
    }

    @Test
    void getColumns_returns_all_columns_of_board() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        UUID column1Id = UUID.randomUUID();
        BoardColumn column1 = TestDataFactory.column(
                column1Id,
                board,
                "col1",
                BigDecimal.valueOf(1000)
        );

        UUID column2Id = UUID.randomUUID();
        BoardColumn column2 = TestDataFactory.column(
                column2Id,
                board,
                "col2",
                BigDecimal.valueOf(2000)
        );


        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(owner);

        when(boardColumnRepository.findAllInHierarchy(boardId, projectId))
                .thenReturn(List.of(column1, column2));

        List<BoardColumnResponse> boardCols = boardColumnService.getColumns(projectId, boardId);

        assertThat(boardCols.size()).isEqualTo(2);

        verify(boardColumnRepository).findAllInHierarchy(boardId, projectId);
    }

    @Test
    void createColumn_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_CREATE)
        ).thenThrow(AccessDeniedException.class);

        CreateBoardColumnRequest request = TestDataFactory.createBoardColumnRequest();

        assertThatThrownBy(() -> boardColumnService.createColumn(projectId, boardId, request ))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardColumnRepository);
    }

    @Test
    void createColumn_throws_resource_not_found_if_board_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_CREATE)
        ).thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenReturn(Optional.empty());

        CreateBoardColumnRequest request = TestDataFactory.createBoardColumnRequest();
        assertThatThrownBy(() -> boardColumnService.createColumn(projectId, boardId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createColumn_returns_created_column_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(userId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        UUID columnId = UUID.randomUUID();
        BoardColumn boardColumn = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(1000));

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_CREATE)
        ).thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenReturn(Optional.of(board));

        when(boardColumnRepository.findMaxPositionByBoardId(boardId)).thenReturn(BigDecimal.ZERO);

        when(boardColumnRepository.save(any(BoardColumn.class))).thenReturn(boardColumn);

        CreateBoardColumnRequest request = TestDataFactory.createBoardColumnRequest();

        BoardColumnResponse response = boardColumnService.createColumn(projectId, boardId, request);

        assertThat(response.name()).isEqualTo(boardColumn.name());

        verify(boardColumnRepository).save(any(BoardColumn.class));

    }

    @Test
    void updateColumn_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenThrow(AccessDeniedException.class);

        UpdateBoardColumnRequest request = TestDataFactory.updateBoardColumnRequest();

        assertThatThrownBy(() ->
                boardColumnService.updateColumn(projectId, boardId, columnId, request )
        ).isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardColumnRepository);
    }

    @Test
    void updateColumn_throws_resource_not_found_when_column_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.empty());

        UpdateBoardColumnRequest request = TestDataFactory.updateBoardColumnRequest();
        assertThatThrownBy(() ->
                boardColumnService.updateColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateColumn_returns_updated_column_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(userId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        UUID columnId = UUID.randomUUID();
        BoardColumn boardColumn = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(1000));

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(boardColumn));


        when(boardColumnRepository.save(any(BoardColumn.class))).thenReturn(boardColumn);

        UpdateBoardColumnRequest request = TestDataFactory.updateBoardColumnRequest();

        BoardColumnResponse response = boardColumnService.updateColumn(
                projectId,
                boardId,
                columnId,
                request
        );

        assertThat(response.name()).isEqualTo(boardColumn.name());

        verify(boardColumnRepository).save(any(BoardColumn.class));
    }

    @Test
    void deleteColumn_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_DELETE)
        ).thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> boardColumnService.deleteColumn(projectId, boardId, columnId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardColumnRepository);
    }

    @Test
    void deleteColumn_throws_resource_not_found_when_column_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_DELETE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardColumnService.deleteColumn(projectId, boardId, columnId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteColumn_deletes_column_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn boardColumn = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(1000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_DELETE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(boardColumn));

        boardColumnService.deleteColumn(projectId, boardId, columnId);

        verify(boardColumnRepository).delete(boardColumn);
    }



}
