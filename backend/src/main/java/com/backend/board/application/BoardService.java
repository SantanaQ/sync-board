package com.backend.board.application;

import com.backend.board.api.BoardListResponse;
import com.backend.board.api.BoardResponse;
import com.backend.board.api.CreateBoardRequest;
import com.backend.board.api.UpdateBoardRequest;
import com.backend.board.domain.Board;
import com.backend.board.infrastructure.BoardRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project.domain.Project;
import com.backend.project.infrastructure.ProjectRepository;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.backend.project_member.domain.ProjectPermission.*;

@Service
public class BoardService {

    private final CurrentUserService currentUserService;
    private final BoardRepository boardRepository;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ProjectRepository projectRepository;

    public BoardService(CurrentUserService currentUserService,
                        BoardRepository boardRepository,
                        ProjectAuthorizationService projectAuthorizationService,
                        ProjectRepository projectRepository) {
        this.currentUserService = currentUserService;
        this.boardRepository = boardRepository;
        this.projectAuthorizationService = projectAuthorizationService;
        this.projectRepository = projectRepository;
    }

    public List<BoardListResponse> getBoards(UUID projectId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

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
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

        Board board = requirePresence(boardId, projectId);

        return new BoardResponse(
                board.name(),
                board.createdAt(),
                board.updatedAt()
        );
    }

    public BoardResponse updateBoard(UUID projectId, UUID boardId, UpdateBoardRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.BOARD_UPDATE
        );

        Board board = requirePresence(boardId, projectId);
        board.setName(request.name());
        board.setUpdatedAt(Instant.now());

        return new BoardResponse(
                board.name(),
                board.createdAt(),
                board.updatedAt()
        );
    }

    public BoardResponse createBoard(UUID projectId, CreateBoardRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                BOARD_CREATE
        );

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

        Board saved = boardRepository.save(board);

        return new BoardResponse(
                saved.name(),
                saved.createdAt(),
                saved.updatedAt()
        );
    }

    @Transactional
    public void deleteBoard(UUID projectId, UUID boardId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                BOARD_DELETE
        );

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
