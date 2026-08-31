package com.backend.board.application;

import com.backend.TestDataFactory;
import com.backend.board.api.BoardListResponse;
import com.backend.board.api.BoardResponse;
import com.backend.board.api.CreateBoardRequest;
import com.backend.board.api.UpdateBoardRequest;
import com.backend.board.domain.Board;
import com.backend.board.infrastructure.BoardRepository;
import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project.domain.Project;
import com.backend.project.infrastructure.ProjectRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    void getBoards_throws_access_denied_if_user_not_member_of_project() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> boardService.getBoards(projectId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardRepository);
    }

    @Test
    void getBoards_returns_all_project_boards() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();

        Project project = TestDataFactory.project(
                projectId,
                "name",
                "description"
        );

        List<Board> projectBoards = List.of(
          new Board("board1", project),
          new Board("board2", project)
        );

        when(currentUserService.get())
                .thenReturn(user);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(owner);

        when(boardRepository.findAllByProjectId(projectId))
                .thenReturn(projectBoards);

        List<BoardListResponse> responses = boardService.getBoards(projectId);

        assertThat(responses.size()).isEqualTo(projectBoards.size());

        verify(boardRepository).findAllByProjectId(projectId);
    }

    @Test
    void getBoard_throws_access_denied_if_user_is_not_member_of_project() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> boardService.getBoard(projectId, boardId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardRepository);
    }

    @Test
    void getBoard_throws_resource_not_found_if_board_not_found() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.getBoard(projectId, boardId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(boardRepository)
                .findByIdAndProjectId(boardId, projectId);

    }

    @Test
    void getBoard_returns_board_when_present_for_user() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);
        Board board = TestDataFactory.board(boardId, projectId, "board");

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requireMembership(projectId, user))
                .thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenReturn(Optional.of(board));

        BoardResponse response = boardService.getBoard(projectId, boardId);

        assertThat(response.name()).isEqualTo(board.name());
    }

    @Test
    void updateBoard_throws_access_denied_if_user_does_not_have_permission() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_UPDATE)
        ).thenThrow(AccessDeniedException.class);

        UpdateBoardRequest request = new UpdateBoardRequest("updatedName");

        assertThatThrownBy(() -> boardService.updateBoard(projectId, boardId, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateBoard_throws_resource_not_found_if_board_not_found() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_UPDATE)
        ).thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
            .thenReturn(Optional.empty());

        UpdateBoardRequest request = new UpdateBoardRequest("updatedName");
        assertThatThrownBy(() -> boardService.updateBoard(projectId, boardId, request))
                .isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void updateBoard_updates_board_under_valid_conditions() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_UPDATE)
        ).thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenReturn(Optional.of(board));

        UpdateBoardRequest request = new UpdateBoardRequest("updatedName");

        BoardResponse response = boardService.updateBoard(projectId, boardId, request);

        assertThat(response.name()).isEqualTo(request.name());
    }

    @Test
    void createBoard_throws_access_denied_if_user_does_not_have_permission() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_CREATE)
        ).thenThrow(AccessDeniedException.class);

        CreateBoardRequest request = new CreateBoardRequest("createdName");
        assertThatThrownBy(() -> boardService.createBoard(projectId, request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardRepository);
    }

    @Test
    void createBoard_throws_resource_not_found_if_project_not_found() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_CREATE)
        ).thenReturn(owner);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        CreateBoardRequest request = new CreateBoardRequest("createdName");
        assertThatThrownBy(() -> boardService.createBoard(projectId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(boardRepository);
    }

    @Test
    void createBoard_creates_board_under_valid_conditions() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        UUID boardId = UUID.randomUUID();
        Board board = TestDataFactory.board(boardId, projectId, "board");

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_CREATE)
        ).thenReturn(owner);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(owner.project()));

        when(boardRepository.save(any(Board.class))).thenReturn(board);

        CreateBoardRequest request = new CreateBoardRequest(board.name());

        boardService.createBoard(projectId, request);

        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void deleteBoard_throws_access_denied_if_user_does_not_have_permission() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.
                requirePermission(projectId, user, ProjectPermission.BOARD_DELETE)
        ).thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> boardService.deleteBoard(projectId, boardId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(boardRepository);
    }

    @Test
    void deleteBoard_throws_resource_not_found_if_board_not_found() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(projectId, user, ProjectPermission.BOARD_DELETE))
                .thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenThrow(ResourceNotFoundException.class);

        assertThatThrownBy(() -> boardService.deleteBoard(projectId, boardId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteBoard_deletes_board_under_valid_conditions() {
        UUID userId = UUID.randomUUID();
        User user = TestDataFactory.user(
                userId,
                "user@email.com",
                "user",
                "password"
        );

        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Board board = TestDataFactory.board(boardId, projectId, "board");

        when(currentUserService.get())
                .thenReturn(user);

        when(projectAuthorizationService.requirePermission(projectId, user, ProjectPermission.BOARD_DELETE))
                .thenReturn(owner);

        when(boardRepository.findByIdAndProjectId(boardId, projectId))
                .thenReturn(Optional.of(board));

        boardService.deleteBoard(projectId, boardId);

        verify(boardRepository).delete(any(Board.class));
    }



}
