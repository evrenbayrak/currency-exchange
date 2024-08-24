package org.project.currencyexchange.service;

import java.math.BigDecimal;
import java.util.Map;

public interface ExternalExchangeApi {
    Map<String, BigDecimal> getExchangeRates(String baseCurrency);
}
