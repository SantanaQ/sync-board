package com.backend;

import com.backend.board.domain.Board;
import com.backend.board_column.domain.BoardColumn;
import com.backend.project.domain.Project;

import java.math.BigDecimal;

public class RepoTestDataFactory {

    public static Project project() {
        return new Project("project", "description");
    }

    public static Board board(Project project) {
        return new Board("board", project);
    }

    public static BoardColumn boardColumn(Board board, BigDecimal position) {
        return new BoardColumn(board, "col", position);
    }

}
