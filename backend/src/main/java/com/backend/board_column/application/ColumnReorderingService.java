package com.backend.board_column.application;

import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.ReorderBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.common.exception.BusinessRuleViolationException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.reordering.PositionCalculator;
import com.backend.common.reordering.ReorderingService;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ColumnReorderingService {

    private static final BigDecimal MIN_POSITION_GAP = new BigDecimal("0.0001");
    private static final BigDecimal POSITION_PADDING = new BigDecimal("1000");
    private static final int POSITION_SCALE = 10; // numeric scale in db

    private final CurrentUserService currentUserService;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ReorderingService<BoardColumn> reorderingService;

    private final BoardColumnRepository boardColumnRepository;

    public ColumnReorderingService(CurrentUserService currentUserService,
                                   ProjectAuthorizationService projectAuthorizationService,
                                   BoardColumnRepository boardColumnRepository) {
        this.boardColumnRepository = boardColumnRepository;
        this.currentUserService = currentUserService;
        this.projectAuthorizationService = projectAuthorizationService;
        PositionCalculator posCalculator = new PositionCalculator(
                MIN_POSITION_GAP,
                POSITION_PADDING,
                POSITION_SCALE
        );
        this.reorderingService = new ReorderingService<>(posCalculator);
    }

    @Transactional
    public BoardColumnResponse reorderColumn(UUID projectId,
                                             UUID boardId,
                                             UUID columnId,
                                             ReorderBoardColumnRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.COLUMN_UPDATE
        );

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
                () -> rebalance(projectId, boardId)
        );

        return toResponse(column);
    }

    private void rebalance(UUID projectId, UUID boardId) {
        List<BoardColumn> cols = boardColumnRepository
                .findAllInHierarchy(boardId, projectId);

        int pos = POSITION_PADDING.intValue();
        for(BoardColumn col : cols) {
            col.setPosition(BigDecimal.valueOf(pos));
            pos += POSITION_PADDING.intValue();
        }
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
