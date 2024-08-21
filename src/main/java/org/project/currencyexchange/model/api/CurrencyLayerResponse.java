package org.project.currencyexchange.model.api;

import lombok.Data;

import java.util.Map;

@Data
public class CurrencyLayerResponse {
    private boolean success;
    private Map<String, Double> quotes;
}
