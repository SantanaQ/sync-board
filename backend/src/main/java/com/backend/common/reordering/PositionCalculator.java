package com.backend.common.reordering;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PositionCalculator {

    private final BigDecimal minGap;
    private final BigDecimal padding;
    private final int scale;

    public PositionCalculator(BigDecimal minGap, BigDecimal padding, int scale) {
        this.minGap = minGap;
        this.padding = padding;
        this.scale = scale;
    }

    public BigDecimal first(BigDecimal after) {
        return after.subtract(padding);
    }

    public BigDecimal last(BigDecimal before) {
        return before.add(padding);
    }

    public BigDecimal only() {
        return padding;
    }

    public BigDecimal between(BigDecimal before, BigDecimal after) {
        return before
                .add(after)
                .divide(
                        BigDecimal.TWO,
                        scale,
                        RoundingMode.HALF_UP
                );
    }

    public boolean needsRebalance(BigDecimal before, BigDecimal after) {
        return after
                .subtract(before)
                .compareTo(minGap) <= 0;
    }

}
