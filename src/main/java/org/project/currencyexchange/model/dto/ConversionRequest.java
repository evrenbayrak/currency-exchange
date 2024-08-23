package org.project.currencyexchange.model.dto;

public record ConversionRequest(
        ExchangeCurrency baseCurrency,
        ExchangeCurrency targetCurrency,
        double amount
) {
}
