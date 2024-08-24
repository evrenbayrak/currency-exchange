package org.project.currencyexchange.model.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CurrencyLayerResponse {
    private boolean success;
    private Map<String, BigDecimal> quotes;
    private ErrorDetail error;

    @Getter
    @Setter
    public static class ErrorDetail {
        private int code;
        private String info;
    }
}
