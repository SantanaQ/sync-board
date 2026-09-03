package com.backend.board_column.infrastructure;

import com.backend.board_column.domain.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, UUID> {

    @Query(
            """
            select bc
            from BoardColumn bc
            where bc.board.id = :boardId
                and bc.board.project.id = :projectId
            order by bc.position asc
            """
    )
    List<BoardColumn> findAllByBoardIdAndProjectId(UUID boardId, UUID projectId);

    @Query(
            """
            select coalesce(max(bc.position), 0)
            from BoardColumn bc
            where bc.board.id = :boardId
            """
    )
    BigDecimal findMaxPositionByBoardId(UUID boardId);

    @Query(
            """
            select bc
            from BoardColumn bc
            where bc.id = :columnId
                and bc.board.id = :boardId
                and bc.board.project.id = :projectId
            """
    )
    Optional<BoardColumn> findInHierarchy(UUID projectId, UUID boardId, UUID columnId);

    @Query(
            """
            select count(bc)
            from BoardColumn bc
            where bc.board.id = :boardId
                and bc.board.project.id = :projectId
            """
    )
    int countByBoardIdAndProjectId(UUID boardId, UUID projectId);
}
