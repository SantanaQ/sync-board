package com.syncboard.common.reordering;

import com.syncboard.board_column.domain.BoardColumn;
import com.syncboard.card.domain.Card;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ReorderingConfiguration {

    @Bean("cardReordering")
    public ReorderingService<Card> cardReorderingService() {
        PositionCalculator calculator = new PositionCalculator(
                new BigDecimal("0.00001"),
                new BigDecimal("1000"),
                10
        );

        return new ReorderingService<>(calculator);
    }

    @Bean("columnReordering")
    public ReorderingService<BoardColumn> columnReorderingService() {
        PositionCalculator calculator = new PositionCalculator(
                new BigDecimal("0.0001"),
                new BigDecimal("1000"),
                10
        );

        return new ReorderingService<>(calculator);
    }

}
