package com.backend.board_column.application;

import com.backend.board.domain.Board;
import com.backend.board.infrastructure.BoardRepository;
import com.backend.board_column.api.BoardColumnResponse;
import com.backend.board_column.api.CreateBoardColumnRequest;
import com.backend.board_column.api.UpdateBoardColumnRequest;
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
import java.util.List;
import java.util.UUID;

@Service
public class BoardColumnService {

    private final CurrentUserService currentUserService;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;

    public BoardColumnService(CurrentUserService currentUserService,
                              ProjectAuthorizationService projectAuthorizationService,
                              BoardRepository boardRepository,
                              BoardColumnRepository boardColumnRepository) {
        this.currentUserService = currentUserService;
        this.projectAuthorizationService = projectAuthorizationService;
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
    }

    public List<BoardColumnResponse> getColumns(UUID projectId, UUID boardId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

        return boardColumnRepository
                .findAllInHierarchy(boardId, projectId).stream()
                .map(boardColumn -> new BoardColumnResponse(
                        boardColumn.id(),
                        boardColumn.name(),
                        boardColumn.position())
                ).toList();
    }

    @Transactional
    public BoardColumnResponse createColumn(UUID projectId,
                                            UUID boardId,
                                            CreateBoardColumnRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.COLUMN_CREATE
        );

        Board board = boardRepository
                .findByIdAndProjectId(boardId, projectId)
                .orElseThrow(() ->
                new ResourceNotFoundException("Board with id " + boardId + " not found.")
        );

        BigDecimal maxPos = boardColumnRepository.findMaxPositionByBoardId(boardId)
                .add(BigDecimal.valueOf(1000));

        BoardColumn column = new BoardColumn(board, request.name(), maxPos);

        BoardColumn saved = boardColumnRepository.save(column);

        return toResponse(saved);
    }

    @Transactional
    public BoardColumnResponse updateColumn(UUID projectId,
                                            UUID boardId,
                                            UUID columnId,
                                            UpdateBoardColumnRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.COLUMN_UPDATE
        );

        BoardColumn column = requirePresence(projectId, boardId, columnId);
        column.setName(request.name());

        BoardColumn saved = boardColumnRepository.save(column);
        return toResponse(saved);
    }

    @Transactional
    public void deleteColumn(UUID projectId,
                             UUID boardId,
                             UUID columnId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.COLUMN_DELETE
        );

        BoardColumn column = requirePresence(projectId, boardId, columnId);

        boardColumnRepository.delete(column);
    }

    private BoardColumn requirePresence(UUID projectId, UUID boardId, UUID columnId) {
        return boardColumnRepository
                .findInHierarchy(projectId, boardId, columnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Column with id " + columnId + " not found.")
                );
    }

    private BoardColumnResponse toResponse(BoardColumn boardColumn) {
        return new BoardColumnResponse(
                boardColumn.id(),
                boardColumn.name(),
                boardColumn.position()
        );
    }


}
