package com.backend.board_column.domain;

import com.backend.board.domain.Board;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "board_columns")
@Entity
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    BigDecimal position;

    protected BoardColumn() {
        // JPA
    }

    public BoardColumn(Board board, String name, BigDecimal position) {
        this.board = board;
        this.name = name;
        this.position = position;
    }

    public UUID id() {
        return id;
    }

    public Board board() {
        return board;
    }

    public String name() {
        return name;
    }

    public BigDecimal position() {
        return position;
    }

    public void setPosition(BigDecimal position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }
}
