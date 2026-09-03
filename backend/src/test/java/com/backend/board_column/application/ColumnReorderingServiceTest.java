package com.backend.board_column.application;

import com.backend.TestDataFactory;
import com.backend.board.domain.Board;
import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.ReorderBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.BusinessRuleViolationException;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ColumnReorderingServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @InjectMocks
    private ColumnReorderingService columnReorderingService;

    @Test
    void reorderColumn_throws_access_denied_if_user_does_not_have_permission() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
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

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();
        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardColumnRepository);
    }

    @Test
    void reorderColumn_throws_resource_not_found_if_column_does_not_exist() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
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

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();
        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void reorderColumn_throws_resource_not_found_if_before_column_does_not_exist() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col1",
                BigDecimal.valueOf(1000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.empty());

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reorderColumn_throws_resource_not_found_if_after_column_does_not_exist() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(2000)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(1000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, afterId))
                .thenReturn(Optional.empty());

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reorderColumn_throws_business_rule_violation_if_before_column_equals_reordered_column() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn afterColumn = TestDataFactory.column(
                afterId,
                board,
                "colAfter",
                BigDecimal.valueOf(2000)
        );


        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, afterId))
                .thenReturn(Optional.of(afterColumn));

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(columnId, afterId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void reorderColumn_throws_business_rule_violation_if_after_column_equals_reordered_column() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(beforeId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(1000)
        );


        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, columnId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void reorderColumn_throws_business_rule_violation_if_before_column_equals_after_column() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(1000)
        );


        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, beforeId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void reorderColumn_throws_business_rule_violation_if_before_column_is_not_positioned_before_after_column() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(4000)
        );

        BoardColumn afterColumn = TestDataFactory.column(
                afterId,
                board,
                "colAfter",
                BigDecimal.valueOf(2000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, afterId))
                .thenReturn(Optional.of(afterColumn));

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void reorderColumn_throws_business_rule_violation_if_board_contains_multiple_columns_and_no_before_or_after_col_provided() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.countByBoardIdAndProjectId(boardId, projectId))
                .thenReturn(2);

        ReorderBoardColumnRequest request
                = new ReorderBoardColumnRequest(null, null);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(projectId, boardId, columnId, request)
        ).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void reorderColumn_sets_first_position_if_board_is_empty() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.countByBoardIdAndProjectId(boardId, projectId))
                .thenReturn(0);

        ReorderBoardColumnRequest request
                = new ReorderBoardColumnRequest(null, null);

        BoardColumnResponse response
                = columnReorderingService.reorderColumn(projectId, boardId, columnId, request);

        assertThat(response.position().compareTo(BigDecimal.valueOf(1000)) == 0);
    }

    @Test
    void reorderColumn_sets_last_position_if_only_before_column_is_provided() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(0)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(1000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        ReorderBoardColumnRequest request
                = new ReorderBoardColumnRequest(beforeId, null);

        BoardColumnResponse response
                = columnReorderingService.reorderColumn(projectId, boardId, columnId, request);

        assertThat(response.position().compareTo(BigDecimal.valueOf(2000)) == 0);
    }

    @Test
    void reorderColumn_sets_first_position_if_only_after_column_is_provided() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn afterColumn = TestDataFactory.column(
                afterId,
                board,
                "colAfter",
                BigDecimal.valueOf(2000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, afterId))
                .thenReturn(Optional.of(afterColumn));

        ReorderBoardColumnRequest request
                = new ReorderBoardColumnRequest(null, afterId);

        BoardColumnResponse response
                = columnReorderingService.reorderColumn(projectId, boardId, columnId, request);

        assertThat(response.position().compareTo(BigDecimal.valueOf(1000)) == 0);
    }

    @Test
    void reorderColumn_sets_position_between_before_and_after_column_if_both_provided() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(1000)
        );

        BoardColumn afterColumn = TestDataFactory.column(
                afterId,
                board,
                "colAfter",
                BigDecimal.valueOf(2000)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, afterId))
                .thenReturn(Optional.of(afterColumn));

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        BoardColumnResponse response
                = columnReorderingService.reorderColumn(projectId, boardId, columnId, request);

        assertThat(response.position().compareTo(BigDecimal.valueOf(1500)) == 0);
    }

    @Test
    void reorderColumn_rebalances_board_if_position_gap_is_too_small() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        User user = TestDataFactory.user(userId);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "col",
                BigDecimal.valueOf(3000)
        );

        BoardColumn beforeColumn = TestDataFactory.column(
                beforeId,
                board,
                "colBefore",
                BigDecimal.valueOf(1000.0001)
        );

        BoardColumn afterColumn = TestDataFactory.column(
                afterId,
                board,
                "colAfter",
                BigDecimal.valueOf(1000.0002)
        );

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(
                projectId,
                user,
                ProjectPermission.COLUMN_UPDATE)
        ).thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, beforeId))
                .thenReturn(Optional.of(beforeColumn));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, afterId))
                .thenReturn(Optional.of(afterColumn));

        when(boardColumnRepository.findAllByBoardIdAndProjectId(boardId, projectId))
                .thenReturn(List.of(beforeColumn, afterColumn, column));

        ReorderBoardColumnRequest request = new ReorderBoardColumnRequest(beforeId, afterId);

        BoardColumnResponse response
                = columnReorderingService.reorderColumn(projectId, boardId, columnId, request);

        assertThat(response.position().compareTo(BigDecimal.valueOf(2000)) == 0);
    }


}
