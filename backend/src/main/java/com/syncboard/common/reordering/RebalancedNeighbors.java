package com.syncboard.common.reordering;

public record RebalancedNeighbors<T extends Reorderable>(
        T before,
        T after
) {
}
