package com.backend.board_column.application;

import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.ReorderBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.reordering.RebalancedNeighbors;
import com.backend.common.reordering.ReorderingService;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ColumnReorderingService {

    private final ProjectAuthorizationService projectAuthorizationService;
    private final ReorderingService<BoardColumn> reorderingService;

    private final BoardColumnRepository boardColumnRepository;

    public ColumnReorderingService(
            ProjectAuthorizationService projectAuthorizationService,
            BoardColumnRepository boardColumnRepository,
            @Qualifier("columnReordering") ReorderingService<BoardColumn> reorderingService
    ) {
        this.boardColumnRepository = boardColumnRepository;
        this.projectAuthorizationService = projectAuthorizationService;
        this.reorderingService = reorderingService;
    }

    @Transactional
    public BoardColumnResponse reorderColumn(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            ReorderBoardColumnRequest request
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.COLUMN_UPDATE);

        BoardColumn column = requirePresence(projectId, boardId, columnId);

        BoardColumn before = request.beforeColumnId() != null
                ? requirePresence(projectId, boardId, request.beforeColumnId())
                : null;

        BoardColumn after = request.afterColumnId() != null
                ? requirePresence(projectId, boardId, request.afterColumnId())
                : null;

        reorderingService.validateNeighbors(column, before, after);

        int colCount = boardColumnRepository.countInHierarchy(boardId, projectId);

        reorderingService.reorder(
                column,
                before,
                after,
                colCount,
                () -> rebalance(projectId, boardId, before, after)
        );

        return toResponse(column);
    }

    private RebalancedNeighbors<BoardColumn> rebalance(
            UUID projectId,
            UUID boardId,
            BoardColumn before,
            BoardColumn after
    ) {
        List<BoardColumn> cols = boardColumnRepository
                .findAllInHierarchy(boardId, projectId);


        int padding = reorderingService.padding();
        int pos = padding;
        for(BoardColumn col : cols) {
            col.setPosition(BigDecimal.valueOf(pos));
            pos += padding;

            if(col.id().equals(before.id())) {
                before = col;
            }

            if(col.id().equals(after.id())) {
                after = col;
            }
        }
        return new RebalancedNeighbors<>(before, after);

    }

    private BoardColumn requirePresence(UUID projectId, UUID boardId, UUID columnId) {
        return boardColumnRepository.findInHierarchy(projectId, boardId, columnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Column with id " + columnId + " not found")
                );
    }

    private BoardColumnResponse toResponse(BoardColumn column) {
        return new BoardColumnResponse(
                column.id(),
                column.name(),
                column.position()
        );
    }


}
