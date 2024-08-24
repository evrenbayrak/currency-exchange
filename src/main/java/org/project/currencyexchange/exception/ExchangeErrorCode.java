package org.project.currencyexchange.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExchangeErrorCode {

    BAD_REQUEST("bad_request", "Invalid request. Please check the input parameters."),
    ENTITY_NOT_FOUND("entity_not_found", "The requested entity was not found."),
    INTERNAL_SERVER_ERROR("internal_server_error", "An unexpected error occurred on the server."),
    API_LIMIT_EXCEEDED("api_limit_exceeded", "The external API rate limit has been exceeded."),
    INVALID_API_CREDENTIALS("invalid_api_credentials", "The API credentials provided are invalid or the user is inactive."),
    INVALID_API_REQUEST("invalid_api_request", "The API request was invalid or improperly formatted."),
    EXTERNAL_API_ERROR("external_api_error", "An error occurred while fetching data from the external API."),
    EXTERNAL_API_TIMEOUT("external_api_timeout", "The request to the external API timed out.");

    private final String code;
    private final String message;
}
