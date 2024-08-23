package org.project.currencyexchange.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.project.currencyexchange.exception.ExchangeErrorResponse;
import org.project.currencyexchange.model.dto.*;
import org.project.currencyexchange.service.ExchangeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Exchange Currency Service", description = "Service to handle currency exchange and transactions")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @Operation(summary = "Get Exchange Rate",
        description = "Returns exchange ratio between requested base and target currencies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful",
                    content = @Content(schema = @Schema(implementation = ExchangeRateDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class)))
    })
    @GetMapping(value = "/exchange-rate/{baseCurrency}/{targetCurrency}")
    public ResponseEntity<ExchangeRateDto> getExchangeRate(
            @PathVariable("baseCurrency") @NotNull(message = "Base currency cannot be null") ExchangeCurrency baseCurrency,
            @PathVariable("targetCurrency") @NotNull(message = "Target currency cannot be null") ExchangeCurrency targetCurrency) {
        ExchangeRateDto response = exchangeService.getExchangeRate(baseCurrency.name(), targetCurrency.name());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Convert Amount",
            description = "Convert a given amount from base currency to target.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful",
                    content = @Content(schema = @Schema(implementation = ConversionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class)))
    })
    @PostMapping(value = "/conversion")
    public ResponseEntity<ConversionResponse> convertAmount(@RequestBody @Valid ConversionRequest conversionRequest){
        ConversionResponse response = exchangeService.convertAmount(conversionRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Transaction Detail",
            description = "Returns requested transaction information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful",
                    content = @Content(schema = @Schema(implementation = ConversionHistoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class)))
    })
    @GetMapping(value = "/conversion-history/{transactionId}")
    public ResponseEntity<ConversionHistoryResponse> getConversionByTransactionId(
            @PathVariable @NotNull(message = "Transaction ID cannot be null") UUID transactionId){
        return ResponseEntity.ok(exchangeService.getConversionByTransactionId(transactionId));
    }

    @Operation(summary = "Conversion History",
            description = "Returns list of conversions by requested date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful",
                    content = @Content(schema = @Schema(implementation = ConversionHistoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ExchangeErrorResponse.class)))
    })
    @GetMapping(value = "/conversion-history")
    public ResponseEntity<Page<ConversionHistoryResponse>> getConversionByDate(
            @RequestParam @NotNull(message = "Date cannot be null") LocalDate date,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size){
        return ResponseEntity.ok(exchangeService.getConversionsByDate(date, PageRequest.of(page, size)));
    }

}
