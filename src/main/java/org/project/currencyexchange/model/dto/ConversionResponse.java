package org.project.currencyexchange.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing the result of a currency conversion")
public record ConversionResponse(
        @Schema(description = "Unique identifier for the transaction")
        String transactionId,
        @Schema(description = "The converted amount in the target currency",
                example = "100.0")
        double conversionAmount
) {
}
