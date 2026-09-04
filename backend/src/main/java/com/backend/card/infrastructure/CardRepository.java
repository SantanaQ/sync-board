package com.backend.card.infrastructure;

import com.backend.card.domain.Card;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query(
            """
            select c
            from Card c
            where c.id = :cardId
                and c.column.id = :columnId
                and c.column.board.id = :boardId
                and c.column.board.project.id = :projectId
            """
    )
    @EntityGraph(attributePaths = "column")
    Optional<Card> findInHierarchy(UUID projectId, UUID boardId, UUID columnId, UUID cardId);

    @Query(
            """
            select c
            from Card c
            where c.column.id = :columnId
                and c.column.board.id = :boardId
                and c.column.board.project.id = :projectId
            order by c.position asc
            """
    )
    @EntityGraph(attributePaths = "column")
    List<Card> findAllInHierarchy(UUID projectId, UUID boardId, UUID columnId);

    @Query(
            """
            select coalesce(max(c.position), 0)
            from Card c
            where c.column.id = :columnId
            """
    )
    BigDecimal findMaxPositionByColumnId(UUID columnId);


    @Query(
            """
            select c
            from Card c
            where c.column.id = :columnId
                and c.column.board.id = :boardId
                and c.column.board.project.id = :projectId
            """
    )
    int countInHierarchy(UUID projectId, UUID boardId, UUID columnId);
}
