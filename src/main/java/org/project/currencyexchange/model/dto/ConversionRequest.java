package org.project.currencyexchange.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request object for converting currency amounts")
public record ConversionRequest(
        @Schema(description = "The base currency from which the amount will be converted",
                example = "USD")
        @NotNull(message = "Base currency cannot be null")
        ExchangeCurrency baseCurrency,
        @Schema(description = "The target currency to which the amount will be converted",
                example = "EUR")
        @NotNull(message = "Target currency cannot be null")
        ExchangeCurrency targetCurrency,
        @Schema(description = "The amount in the base currency that needs to be converted",
                example = "10.0")
        @Positive(message = "Amount must be greater than zero")
        double amount
) {
}
