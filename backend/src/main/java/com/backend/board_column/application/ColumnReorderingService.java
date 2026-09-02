package com.backend.board_column.application;

import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.ReorderBoardColumnRequest;
import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
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
    private static final int POSITION_PADDING = 1000;

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


        BoardColumn before =
                request.beforeColumnId() != null
                        ? requirePresence(projectId, boardId, columnId)
                        : null;

        BoardColumn after =
                request.afterColumnId() != null
                        ? requirePresence(projectId, boardId, columnId)
                        : null;

        BigDecimal newPosition;

        if (before == null && after == null) {
            // only column on board
            newPosition = BigDecimal.valueOf(POSITION_PADDING);

        } else if (before == null) {
            // move to first position
            newPosition = after.position()
                    .subtract(BigDecimal.valueOf(POSITION_PADDING));

        } else if (after == null) {
            // move to last position
            newPosition = before.position()
                    .add(BigDecimal.valueOf(POSITION_PADDING));

        } else {

            BigDecimal gap = after.position()
                    .subtract(before.position());

            if (gap.compareTo(MIN_POSITION_GAP) < 0) {

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
                            RoundingMode.HALF_UP
                    );
        }

        column.setPosition(newPosition);

        return toResponse(column);
    }

    private void rebalanceBoard(UUID projectId, UUID boardId) {
        List<BoardColumn> cols = boardColumnRepository
                .findAllByBoardIdAndProjectId(boardId, projectId);

        int pos = POSITION_PADDING;
        for(BoardColumn col : cols) {
            col.setPosition(BigDecimal.valueOf(pos));
            pos += POSITION_PADDING;
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
