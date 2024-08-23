package org.project.currencyexchange.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExchangeErrorCode {

    BAD_REQUEST("bad_request", "Can not validate request"),
    ENTITY_NOT_FOUND("entity_not_found", "Entity not found"),
    INTERNAL_SERVER_ERROR("server_error", "Unexpected server error occurred");

    private final String code;
    private final String message;
}
