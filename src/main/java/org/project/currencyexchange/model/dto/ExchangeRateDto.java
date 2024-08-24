package org.project.currencyexchange.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Representing the exchange rate between two currencies")
public record ExchangeRateDto(
        @Schema(description = "The exchange rate from the base currency to the target currency",
                example = "1.15")
        BigDecimal rate) {
}
