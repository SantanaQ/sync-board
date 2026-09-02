package com.backend.board_column.api;

import com.backend.board_column.application.BoardColumnService;
import com.backend.board_column.application.ColumnReorderingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/boards/{boardId}/columns")
public class BoardColumnController {

    private final BoardColumnService boardColumnService;
    private final ColumnReorderingService columnReorderingService;

    public BoardColumnController(BoardColumnService boardColumnService,
                                 ColumnReorderingService columnReorderingService) {
        this.boardColumnService = boardColumnService;
        this.columnReorderingService = columnReorderingService;
    }


    @GetMapping
    public List<BoardColumnResponse> getColumns(@PathVariable("projectId") UUID projectId,
                                                @PathVariable("boardId") UUID boardId) {
        return boardColumnService.getColumns(projectId, boardId);
    }

    @PostMapping
    public BoardColumnResponse createColumn(@PathVariable("projectId") UUID projectId,
                                            @PathVariable("boardId") UUID boardId,
                                            @Valid @RequestBody CreateBoardColumnRequest request) {
        return boardColumnService.createColumn(projectId, boardId, request);
    }

    @PutMapping("/{columnId}")
    public BoardColumnResponse updateColumn(@PathVariable("projectId") UUID projectId,
                                            @PathVariable("boardId") UUID boardId,
                                            @PathVariable("columnId") UUID columnId,
                                            @Valid @RequestBody UpdateBoardColumnRequest request) {
        return boardColumnService.updateColumn(projectId, boardId, columnId, request);
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable("projectId") UUID projectId,
                                             @PathVariable("boardId") UUID boardId,
                                             @PathVariable("columnId") UUID columnId
                                             ) {
        boardColumnService.deleteColumn(projectId, boardId, columnId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{columnId}/reorder")
    public BoardColumnResponse reorderColumn(@PathVariable("projectId") UUID projectId,
                                             @PathVariable("boardId") UUID boardId,
                                             @PathVariable("columnId") UUID columnId,
                                             @RequestBody ReorderBoardColumnRequest request
                                             ) {
        return columnReorderingService.reorderColumn(projectId, boardId, columnId, request);
    }

}
