package com.syncboard.common.reordering;

import com.syncboard.common.exception.BusinessRuleViolationException;

import java.util.function.Supplier;

public class ReorderingService<T extends Reorderable> {

    private final PositionCalculator positionCalculator;

    public ReorderingService(PositionCalculator positionCalculator) {
        this.positionCalculator = positionCalculator;
    }

    public void reorder(
            T entity,
            T before,
            T after,
            long count,
            Supplier<RebalancedNeighbors<T>> rebalance
    ) {
        if (before == null && after == null) {
            if (count > 1) {
                throw new BusinessRuleViolationException(
                        "Predecessor or successor must be provided when parent element is not empty."
                );
            }

            entity.setPosition(positionCalculator.only());
            return;
        }

        if (before == null) {
            entity.setPosition(
                    positionCalculator.first(after.position())
            );
            return;
        }

        if (after == null) {
            entity.setPosition(
                    positionCalculator.last(before.position())
            );
            return;
        }

        if (positionCalculator.needsRebalance(
                before.position(),
                after.position()
        )) {
            // update entities to ensure rebalanced position is visible
            RebalancedNeighbors<T> neighbors = rebalance.get();
            before = neighbors.before();
            after = neighbors.after();
        }

        entity.setPosition(
                positionCalculator.between(
                        before.position(),
                        after.position()
                )
        );
    }

    public void validateNeighbors(T entity, T before, T after) {
        if (before != null && before.id().equals(entity.id())) {
            throw new BusinessRuleViolationException(
                    "Entity cannot be its own predecessor."
            );
        }

        if (after != null && after.id().equals(entity.id())) {
            throw new BusinessRuleViolationException(
                    "Entity cannot be its own successor."
            );
        }

        if (before != null
                && after != null
                && before.id().equals(after.id())) {
            throw new BusinessRuleViolationException(
                    "Predecessor and successor must be different."
            );
        }

        if(before != null
                && after != null
                && before.position().compareTo(after.position()) >= 0) {

            throw new BusinessRuleViolationException(
                    "Predecessor must be positioned before successor."
            );
        }
    }

    public int padding() {
        return positionCalculator.paddingValue();
    }

}
