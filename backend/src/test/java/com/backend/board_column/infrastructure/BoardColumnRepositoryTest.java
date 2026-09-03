package com.backend.board_column.infrastructure;

import com.backend.RepoTestDataFactory;
import com.backend.RepositoryTestConfig;
import com.backend.TestDataFactory;
import com.backend.board.domain.Board;
import com.backend.board.infrastructure.BoardRepository;
import com.backend.board_column.domain.BoardColumn;
import com.backend.project.domain.Project;
import com.backend.project.infrastructure.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardColumnRepositoryTest extends RepositoryTestConfig {

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void findAllByBoardIdAndProjectId_returns_columns_ordered_by_position() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        BoardColumn column1 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.035));
        BoardColumn column2 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.045));
        BoardColumn column3 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(2000));


        projectRepository.save(project);
        boardRepository.save(board);
        boardColumnRepository.save(column1);
        boardColumnRepository.save(column2);
        boardColumnRepository.save(column3);

        List<BoardColumn> cols
                = boardColumnRepository.findAllByBoardIdAndProjectId(board.id(), project.id());

        assertThat(cols.get(0).position().compareTo(cols.get(1).position())).isLessThan(0);
        assertThat(cols.get(1).position().compareTo(cols.get(2).position())).isLessThan(0);
    }

    @Test
    void findMaxPositionByBoardId_returns_highest_position() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        BoardColumn column1 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.035));
        BoardColumn column2 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.045));
        BoardColumn column3 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(2000));

        projectRepository.save(project);
        boardRepository.save(board);
        boardColumnRepository.save(column1);
        boardColumnRepository.save(column2);
        boardColumnRepository.save(column3);

        BigDecimal maxPos = boardColumnRepository.findMaxPositionByBoardId(board.id());

        assertThat(maxPos.compareTo(column3.position())).isEqualTo(0);
    }

    @Test
    void findMaxPositionByBoardId_returns_zero_when_board_is_empty() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        projectRepository.save(project);
        boardRepository.save(board);

        BigDecimal maxPos = boardColumnRepository.findMaxPositionByBoardId(board.id());

        assertThat(maxPos.compareTo(BigDecimal.ZERO)).isEqualTo(0);
    }

    @Test
    void findInHierarchy_returns_column_when_project_board_and_column_match() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        BoardColumn column = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.035));

        projectRepository.save(project);
        boardRepository.save(board);
        boardColumnRepository.save(column);

        Optional<BoardColumn> col
                = boardColumnRepository.findInHierarchy(project.id(), board.id(), column.id());

        assertThat(col.isPresent()).isTrue();
    }

    @Test
    void findInHierarchy_returns_empty_when_column_belongs_to_different_project() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        BoardColumn column = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.035));

        projectRepository.save(project);
        boardRepository.save(board);
        boardColumnRepository.save(column);

        Optional<BoardColumn> col = boardColumnRepository.findInHierarchy(
                UUID.randomUUID(),
                board.id(),
                column.id()
        );

        assertThat(col.isPresent()).isFalse();
    }

    @Test
    void findInHierarchy_returns_empty_when_column_belongs_to_different_board() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        BoardColumn column = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.035));

        projectRepository.save(project);
        boardRepository.save(board);
        boardColumnRepository.save(column);

        Optional<BoardColumn> col = boardColumnRepository.findInHierarchy(
                project.id(),
                UUID.randomUUID(),
                column.id()
        );

        assertThat(col.isPresent()).isFalse();
    }

    @Test
    void countByBoardIdAndProjectId_returns_number_of_columns() {
        Project project = RepoTestDataFactory.project();

        Board board = RepoTestDataFactory.board(project);

        BoardColumn column1 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.035));
        BoardColumn column2 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(1250.045));
        BoardColumn column3 = RepoTestDataFactory.boardColumn(board, BigDecimal.valueOf(2000));

        projectRepository.save(project);
        boardRepository.save(board);
        boardColumnRepository.save(column1);
        boardColumnRepository.save(column2);
        boardColumnRepository.save(column3);

        int count = boardColumnRepository.countByBoardIdAndProjectId(board.id(), project.id());

        assertThat(count).isEqualTo(3);
    }
}
