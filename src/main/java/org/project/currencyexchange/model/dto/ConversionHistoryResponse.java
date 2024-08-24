package org.project.currencyexchange.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Response object containing details of a currency conversion transaction")
public record ConversionHistoryResponse(
        @Schema(description = "Unique identifier for the transaction")
        String transactionId,
        @Schema(description = "The base currency used for conversion",
                example = "USD")
        ExchangeCurrency baseCurrency,
        @Schema(description = "The target currency after conversion",
                example = "EUR")
        ExchangeCurrency targetCurrency,
        @Schema(description = "The amount in the base currency",
                example = "100.0")
        BigDecimal baseAmount,
        @Schema(description = "The converted amount in the target currency",
                example = "100.0")
        BigDecimal conversionAmount,
        @Schema(description = "The date and time when the conversion occurred")
        LocalDateTime conversionDate
) {
}
