package com.syncboard.board.application;

import com.syncboard.board.api.BoardListResponse;
import com.syncboard.board.api.BoardResponse;
import com.syncboard.board.api.CreateBoardRequest;
import com.syncboard.board.api.UpdateBoardRequest;
import com.syncboard.board.domain.Board;
import com.syncboard.board.infrastructure.BoardRepository;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.project.domain.Project;
import com.syncboard.project.infrastructure.ProjectRepository;
import com.syncboard.project_member.application.ProjectAuthorizationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.syncboard.project_member.domain.ProjectPermission.*;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ProjectRepository projectRepository;

    public BoardService(
            BoardRepository boardRepository,
            ProjectAuthorizationService projectAuthorizationService,
            ProjectRepository projectRepository)
    {
        this.boardRepository = boardRepository;
        this.projectAuthorizationService = projectAuthorizationService;
        this.projectRepository = projectRepository;
    }

    public List<BoardListResponse> getBoards(UUID projectId) {
        projectAuthorizationService.requirePermission(projectId, BOARD_VIEW);

        return boardRepository.findAllByProjectId(projectId)
                .stream()
                .map(board -> new BoardListResponse(
                        board.name(),
                        board.createdAt(),
                        board.updatedAt()
                ))
                .toList();
    }

    public BoardResponse getBoard(UUID projectId, UUID boardId) {
        projectAuthorizationService.requirePermission(projectId, BOARD_VIEW);

        Board board = requirePresence(boardId, projectId);

        return new BoardResponse(
                board.name(),
                board.createdAt(),
                board.updatedAt()
        );
    }

    @Transactional
    public BoardResponse updateBoard(
            UUID projectId,
            UUID boardId,
            UpdateBoardRequest request
    ) {
        projectAuthorizationService.requirePermission(projectId, BOARD_UPDATE);

        Board board = requirePresence(boardId, projectId);
        board.setName(request.name());
        board.setUpdatedAt(Instant.now());

        return new BoardResponse(
                board.name(),
                board.createdAt(),
                board.updatedAt()
        );
    }

    @Transactional
    public BoardResponse createBoard(UUID projectId, CreateBoardRequest request) {
        projectAuthorizationService.requirePermission(projectId, BOARD_CREATE);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project with id " + projectId + " not found."
                        )
                );

        Board board = new Board(
                request.name(),
                project
        );

        boardRepository.save(board);

        return new BoardResponse(
                board.name(),
                board.createdAt(),
                board.updatedAt()
        );
    }

    @Transactional
    public void deleteBoard(UUID projectId, UUID boardId) {
        projectAuthorizationService.requirePermission(projectId, BOARD_DELETE);

        Board board = requirePresence(boardId, projectId);

        boardRepository.delete(board);
    }

    private Board requirePresence(UUID boardId, UUID projectId) {
        return boardRepository
                .findByIdAndProjectId(boardId, projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Board with id " + boardId + " not found."
                        )
                );
    }



}
