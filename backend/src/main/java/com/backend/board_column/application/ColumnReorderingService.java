package com.backend.board_column.application;

import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.ReorderBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.common.exception.BusinessRuleViolationException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class ColumnReorderingService {

    private static final BigDecimal MIN_POSITION_GAP = new BigDecimal("0.0001");
    private static final BigDecimal POSITION_PADDING = new BigDecimal("1000");
    private static final int POSITION_SCALE = 10; // numeric scale in db

    private final CurrentUserService currentUserService;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final BoardColumnRepository boardColumnRepository;

    public ColumnReorderingService(CurrentUserService currentUserService,
                                   ProjectAuthorizationService projectAuthorizationService,
                                   BoardColumnRepository boardColumnRepository) {
        this.boardColumnRepository = boardColumnRepository;
        this.currentUserService = currentUserService;
        this.projectAuthorizationService = projectAuthorizationService;
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

        validateNeighbors(column, before, after);

        BigDecimal newPosition;

        if (before == null && after == null) {
            if (boardColumnRepository.countByBoardIdAndProjectId(boardId, projectId) > 1) {
                throw new BusinessRuleViolationException(
                        "Before or after column must be provided when the board contains multiple columns"
                );
            }

            // only column on board
            newPosition = POSITION_PADDING;

        } else if (before == null) {
            // move to first position
            newPosition = after.position()
                    .subtract(POSITION_PADDING);

        } else if (after == null) {
            // move to last position
            newPosition = before.position()
                    .add(POSITION_PADDING);

        } else {
            BigDecimal gap = after.position()
                    .subtract(before.position());

            if (gap.compareTo(MIN_POSITION_GAP) <= 0) {
                rebalanceBoard(projectId, boardId);

                before = requirePresence(
                        projectId, boardId, before.id()
                );

                after = requirePresence(
                        projectId, boardId, after.id()
                );
            }

            newPosition = before.position()
                    .add(after.position())
                    .divide(
                            BigDecimal.TWO,
                            POSITION_SCALE,
                            RoundingMode.HALF_UP
                    );
        }

        column.setPosition(newPosition);

        return toResponse(column);
    }

    private void rebalanceBoard(UUID projectId, UUID boardId) {
        List<BoardColumn> cols = boardColumnRepository
                .findAllByBoardIdAndProjectId(boardId, projectId);

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

    private void validateNeighbors(BoardColumn column, BoardColumn before, BoardColumn after) {
        if (before != null && before.id().equals(column.id())) {
            throw new BusinessRuleViolationException(
                    "Column cannot be its own predecessor"
            );
        }

        if (after != null && after.id().equals(column.id())) {
            throw new BusinessRuleViolationException(
                    "Column cannot be its own successor"
            );
        }

        if (before != null
                && after != null
                && before.id().equals(after.id())) {
            throw new BusinessRuleViolationException(
                    "Before and after column must be different"
            );
        }

        if(before != null
                && after != null
                && before.position().compareTo(after.position()) >= 0) {

            throw new BusinessRuleViolationException(
                    "Before column must be positioned before after column"
            );
        }

    }

    private BoardColumnResponse toResponse(BoardColumn column) {
        return new BoardColumnResponse(
                column.id(),
                column.name(),
                column.position()
        );
    }


}
