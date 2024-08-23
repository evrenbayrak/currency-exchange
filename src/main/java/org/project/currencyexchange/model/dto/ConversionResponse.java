package org.project.currencyexchange.model.dto;

public record ConversionResponse(
        String transactionId,
        double conversionAmount
) {
}
