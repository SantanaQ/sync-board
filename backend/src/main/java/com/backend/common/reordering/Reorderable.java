package com.backend.common.reordering;

import java.math.BigDecimal;
import java.util.UUID;

public interface Reorderable {

    UUID id();
    BigDecimal position();
    void setPosition(BigDecimal position);
}
