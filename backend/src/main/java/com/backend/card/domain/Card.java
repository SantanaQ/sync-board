package com.backend.card.domain;

import com.backend.board_column.domain.BoardColumn;
import com.backend.common.reordering.Reorderable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Table(name = "cards")
@Entity
public class Card implements Reorderable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "column_id")
    private BoardColumn column;

    @NotNull
    @Column
    private String title;

    @Column
    private String description;

    @NotNull
    @Column
    private BigDecimal position;

    @Column
    private BigInteger version;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    protected Card() {
        // JPA
    }

    public Card(BoardColumn column, String title, String description, BigDecimal position) {
        this.column = column;
        this.title = title;
        this.description = description;
        this.position = position;
        this.version = BigInteger.ONE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID id() {
        return id;
    }

    public BoardColumn column() {
        return column;
    }

    public @NotNull String title() {
        return title;
    }

    public String description() {
        return description;
    }

    @Override
    public @NotNull BigDecimal position() {
        return position;
    }

    public BigInteger version() {
        return version;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPosition(@NotNull BigDecimal position) {
        this.position = position;
    }

    public void setVersion(BigInteger version) {
        this.version = version;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
