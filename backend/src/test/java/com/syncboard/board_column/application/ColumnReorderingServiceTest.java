package com.syncboard.board_column.application;

import com.syncboard.TestDataFactory;
import com.syncboard.board.domain.Board;
import com.syncboard.board_column.api.ReorderBoardColumnRequest;
import com.syncboard.board_column.domain.BoardColumn;
import com.syncboard.board_column.infrastructure.BoardColumnRepository;
import com.syncboard.common.exception.AccessDeniedException;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.common.reordering.ReorderingService;
import com.syncboard.project_member.application.ProjectAuthorizationService;
import com.syncboard.project_member.domain.ProjectPermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnReorderingServiceTest {

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private ReorderingService<BoardColumn> reorderingService;

    @InjectMocks
    private ColumnReorderingService columnReorderingService;

    @Test
    void reorderColumn_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(
                projectId,
                ProjectPermission.COLUMN_UPDATE
        )).thenThrow(AccessDeniedException.class);

        ReorderBoardColumnRequest request =
                new ReorderBoardColumnRequest(null, null);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(
                        projectId,
                        boardId,
                        columnId,
                        request
                )
        )
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardColumnRepository);
        verifyNoInteractions(reorderingService);
    }

    @Test
    void reorderColumn_throws_resource_not_found_if_column_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        when(boardColumnRepository.findInHierarchy(
                projectId,
                boardId,
                columnId
        )).thenReturn(Optional.empty());

        ReorderBoardColumnRequest request =
                new ReorderBoardColumnRequest(null, null);

        assertThatThrownBy(() ->
                columnReorderingService.reorderColumn(
                        projectId,
                        boardId,
                        columnId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(reorderingService);
    }

    @Test
    void reorderColumn_delegates_reordering_to_reordering_service() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        Board board = TestDataFactory.board(
                boardId,
                projectId,
                "board"
        );

        BoardColumn column = TestDataFactory.column(
                columnId,
                board,
                "column",
                BigDecimal.valueOf(3000)
        );

        BoardColumn before = TestDataFactory.column(
                beforeId,
                board,
                "before",
                BigDecimal.valueOf(1000)
        );

        BoardColumn after = TestDataFactory.column(
                afterId,
                board,
                "after",
                BigDecimal.valueOf(2000)
        );

        when(boardColumnRepository.findInHierarchy(
                projectId, boardId, columnId
        )).thenReturn(Optional.of(column));

        when(boardColumnRepository.findInHierarchy(
                projectId, boardId, beforeId
        )).thenReturn(Optional.of(before));

        when(boardColumnRepository.findInHierarchy(
                projectId, boardId, afterId
        )).thenReturn(Optional.of(after));

        when(boardColumnRepository.countInHierarchy(
                boardId,
                projectId
        )).thenReturn(3);

        ReorderBoardColumnRequest request =
                new ReorderBoardColumnRequest(beforeId, afterId);

        columnReorderingService.reorderColumn(
                projectId,
                boardId,
                columnId,
                request
        );

        verify(reorderingService).validateNeighbors(
                column,
                before,
                after
        );

        verify(reorderingService).reorder(
                eq(column),
                eq(before),
                eq(after),
                eq(3L),
                any()
        );
    }
}
