package org.project.currencyexchange.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "Custom error response")
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeErrorResponse{

    @Schema(description = "Error code", example = "bad_request")
    private final String errorCode;
    @Schema(description = "Error message", example = "bad_request")
    private final String errorMessage;
    @Schema(description = "Detailed error description")
    private final Detail detail;

    @Schema(description = "Current time")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyy hh:mm:ss"
    )
    private final LocalDateTime timestamp;


    public static ExchangeErrorResponseBuilder builder() {
        return new ExchangeErrorResponseBuilder();
    }

    public static class ExchangeErrorResponseBuilder {
        private String errorCode;
        private String errorMessage;
        private Detail detail;
        private LocalDateTime timestamp;

        ExchangeErrorResponseBuilder() {
        }

        public ExchangeErrorResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ExchangeErrorResponseBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public ExchangeErrorResponseBuilder detail(Detail detail) {
            this.detail = detail;
            return this;
        }

        public ExchangeErrorResponse build() {
            this.timestamp = LocalDateTime.now();
            return new ExchangeErrorResponse(this.errorCode, this.errorMessage, this.detail, this.timestamp);
        }

    }
}
