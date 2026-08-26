package com.backend.board.infrastructure;

import com.backend.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardRepository extends JpaRepository<Board, UUID> {
    List<Board> findAllByProjectId(UUID projectId);
    Optional<Board> findByIdAndProjectId(UUID projectId, UUID boardId);

}
