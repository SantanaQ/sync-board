package com.syncboard.board.api;

import com.syncboard.board.application.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    @GetMapping("/{id}")
    public BoardResponse getBoard(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("id") UUID boardId
    ) {
        return boardService.getBoard(projectId, boardId);
    }

    @GetMapping
    public List<BoardListResponse> getBoards(
            @PathVariable("projectId") UUID projectId
    ) {
        return boardService.getBoards(projectId);
    }

    @PostMapping
    public BoardResponse createBoard(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody CreateBoardRequest request
    ) {
        return boardService.createBoard(projectId, request);
    }

    @PutMapping("/{id}")
    public BoardResponse updateBoard(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("id") UUID boardId,
            @Valid @RequestBody UpdateBoardRequest request
    ) {
        return boardService.updateBoard(projectId, boardId, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("id") UUID boardId
    ) {
        boardService.deleteBoard(projectId, boardId);

        return ResponseEntity.noContent().build();
    }
}
