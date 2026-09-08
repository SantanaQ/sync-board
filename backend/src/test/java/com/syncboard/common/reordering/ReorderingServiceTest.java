package com.syncboard.common.reordering;

import com.syncboard.common.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ReorderingServiceTest {

    private static final BigDecimal MIN_POSITION_GAP = new BigDecimal("0.0001");
    private static final BigDecimal POSITION_PADDING = new BigDecimal("1000");
    private static final int POSITION_SCALE = 10;

    private final PositionCalculator positionCalculator = new PositionCalculator(
            MIN_POSITION_GAP,
            POSITION_PADDING,
            POSITION_SCALE
    );

    private final ReorderingService<TestReorderable> reorderingService =
            new ReorderingService<>(positionCalculator);

    @Test
    void reorder_throws_if_multiple_entities_exist_and_no_neighbors_are_provided() {
        TestReorderable entity = entity(3000);

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        assertThatThrownBy(() ->
                reorderingService.reorder(
                        entity,
                        null,
                        null,
                        2,
                        rebalance
                )
        )
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage(
                        "Predecessor or successor must be provided when parent element is not empty."
                );
    }

    @Test
    void reorder_sets_only_position_if_no_neighbors_are_provided_and_entity_is_alone() {
        TestReorderable entity = entity(3000);

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        reorderingService.reorder(
                entity,
                null,
                null,
                1,
                rebalance
        );

        assertThat(entity.position())
                .isEqualByComparingTo("1000");
    }

    @Test
    void reorder_sets_only_position_if_count_is_zero() {
        TestReorderable entity = entity(3000);

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        reorderingService.reorder(
                entity,
                null,
                null,
                0,
                rebalance
        );

        assertThat(entity.position())
                .isEqualByComparingTo("1000");
    }

    @Test
    void reorder_sets_first_position_if_only_after_is_provided() {
        TestReorderable entity = entity(3000);
        TestReorderable after = entity(2000);

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        reorderingService.reorder(
                entity,
                null,
                after,
                3,
                rebalance
        );

        assertThat(entity.position())
                .isEqualByComparingTo("1000");
    }

    @Test
    void reorder_sets_last_position_if_only_before_is_provided() {
        TestReorderable entity = entity(0);
        TestReorderable before = entity(1000);

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        reorderingService.reorder(
                entity,
                before,
                null,
                3,
                rebalance
        );

        assertThat(entity.position())
                .isEqualByComparingTo("2000");
    }

    @Test
    void reorder_sets_position_between_before_and_after() {
        TestReorderable entity = entity(3000);
        TestReorderable before = entity(1000);
        TestReorderable after = entity(2000);

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        reorderingService.reorder(
                entity,
                before,
                after,
                3,
                rebalance
        );

        assertThat(entity.position())
                .isEqualByComparingTo("1500");
    }

    @Test
    void reorder_uses_rebalanced_neighbor_positions() {
        TestReorderable entity = entity(3000);

        TestReorderable before = entity("1000.0001");
        TestReorderable after = entity("1000.0002");

        TestReorderable rebalancedBefore = entity(1000);
        TestReorderable rebalancedAfter = entity(2000);

        RebalancedNeighbors<TestReorderable> rebalancedNeighbors =
                new RebalancedNeighbors<>(
                        rebalancedBefore,
                        rebalancedAfter
                );

        @SuppressWarnings("unchecked")
        Supplier<RebalancedNeighbors<TestReorderable>> rebalance =
                mock(Supplier.class);

        when(rebalance.get()).thenReturn(rebalancedNeighbors);

        reorderingService.reorder(
                entity,
                before,
                after,
                3,
                rebalance
        );

        verify(rebalance).get();

        assertThat(entity.position())
                .isEqualByComparingTo("1500");
    }

    @Test
    void validateNeighbors_throws_if_entity_is_before() {
        TestReorderable entity = entity(2000);

        assertThatThrownBy(() ->
                reorderingService.validateNeighbors(
                        entity,
                        entity,
                        null
                )
        )
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Entity cannot be its own predecessor.");
    }

    @Test
    void validateNeighbors_throws_if_entity_is_after() {
        TestReorderable entity = entity(2000);

        assertThatThrownBy(() ->
                reorderingService.validateNeighbors(
                        entity,
                        null,
                        entity
                )
        )
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Entity cannot be its own successor.");
    }

    @Test
    void validateNeighbors_throws_if_before_and_after_are_same() {
        TestReorderable entity = entity(3000);
        TestReorderable neighbor = entity(1000);

        assertThatThrownBy(() ->
                reorderingService.validateNeighbors(
                        entity,
                        neighbor,
                        neighbor
                )
        )
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Predecessor and successor must be different.");
    }

    @Test
    void validateNeighbors_throws_if_before_is_not_before_after() {
        TestReorderable entity = entity(3000);
        TestReorderable before = entity(2000);
        TestReorderable after = entity(1000);

        assertThatThrownBy(() ->
                reorderingService.validateNeighbors(
                        entity,
                        before,
                        after
                )
        )
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage(
                        "Predecessor must be positioned before successor."
                );
    }

    @Test
    void validateNeighbors_accepts_valid_neighbors() {
        TestReorderable entity = entity(3000);
        TestReorderable before = entity(1000);
        TestReorderable after = entity(2000);

        reorderingService.validateNeighbors(
                entity,
                before,
                after
        );
    }

    private TestReorderable entity(int position) {
        return entity(BigDecimal.valueOf(position));
    }

    private TestReorderable entity(String position) {
        return entity(new BigDecimal(position));
    }

    private TestReorderable entity(BigDecimal position) {
        return new TestReorderable(
                UUID.randomUUID(),
                position
        );
    }

    private static class TestReorderable implements Reorderable {

        private final UUID id;
        private BigDecimal position;

        private TestReorderable(UUID id, BigDecimal position) {
            this.id = id;
            this.position = position;
        }

        @Override
        public UUID id() {
            return id;
        }

        @Override
        public BigDecimal position() {
            return position;
        }

        @Override
        public void setPosition(BigDecimal position) {
            this.position = position;
        }
    }
}