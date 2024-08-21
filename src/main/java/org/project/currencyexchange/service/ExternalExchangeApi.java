package org.project.currencyexchange.service;

import org.project.currencyexchange.model.dto.ExchangeRateDto;

public interface ExternalExchangeApi {
    ExchangeRateDto getExchangeRate(String baseCurrency, String targetCurrency);
}
