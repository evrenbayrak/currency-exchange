package org.project.currencyexchange.exception;

import lombok.Getter;

@Getter
public class ExchangeCurrencyException extends RuntimeException{

    private final ExchangeErrorCode errorCode;

    public ExchangeCurrencyException(ExchangeErrorCode errorCode) {
        this(errorCode, null);
    }

    public ExchangeCurrencyException(ExchangeErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
