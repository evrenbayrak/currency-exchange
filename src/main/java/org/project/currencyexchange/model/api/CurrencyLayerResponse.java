package org.project.currencyexchange.model.api;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CurrencyLayerResponse {
    private boolean success;
    private Map<String, BigDecimal> quotes;
}
