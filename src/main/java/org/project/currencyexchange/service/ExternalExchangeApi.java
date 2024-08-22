package org.project.currencyexchange.service;

import java.util.Map;

public interface ExternalExchangeApi {
    Map<String, Double> getExchangeRates(String baseCurrency);
}
