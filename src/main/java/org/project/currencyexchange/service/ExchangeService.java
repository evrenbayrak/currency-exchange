package org.project.currencyexchange.service;

import lombok.RequiredArgsConstructor;
import org.project.currencyexchange.model.dto.ExchangeRateDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExternalExchangeApi externalExchangeApi;

    public ExchangeRateDto getExchangeRate(String baseCurrency, String targetCurrency) {
        double exchangeRate = externalExchangeApi.getExchangeRates(baseCurrency).get(baseCurrency+targetCurrency);
        return new ExchangeRateDto(exchangeRate);
    }

}
