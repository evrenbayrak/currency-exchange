package org.project.currencyexchange.model.dto;

import java.time.LocalDateTime;

public record ConversionHistoryResponse(
        String transactionId,
        ExchangeCurrency baseCurrency,
        ExchangeCurrency targetCurrency,
        double baseAmount,
        double conversionAmount,
        LocalDateTime conversionDate
) {
}
