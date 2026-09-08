package com.backend.common.reordering;

public record RebalancedNeighbors<T extends Reorderable>(
        T before,
        T after
) {
}
