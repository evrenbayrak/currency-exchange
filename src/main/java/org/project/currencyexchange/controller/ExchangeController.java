package org.project.currencyexchange.controller;

import lombok.RequiredArgsConstructor;
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
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping(value = "/exchange-rate/{baseCurrency}/{targetCurrency}")
    public ResponseEntity<ExchangeRateDto> getExchangeRate(@PathVariable("baseCurrency") ExchangeCurrency baseCurrency,
                                                           @PathVariable("targetCurrency") ExchangeCurrency targetCurrency) {
        ExchangeRateDto response = exchangeService.getExchangeRate(baseCurrency.name(), targetCurrency.name());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/conversion")
    public ResponseEntity<ConversionResponse> convertAmount(@RequestBody ConversionRequest conversionRequest){
        ConversionResponse response = exchangeService.convertAmount(conversionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/conversion-history/{transactionId}")
    public ResponseEntity<ConversionHistoryResponse> getConversionByTransactionId(@PathVariable UUID transactionId){
        return ResponseEntity.ok(exchangeService.getConversionByTransactionId(transactionId));
    }

    @GetMapping(value = "/conversion-history")
    public ResponseEntity<Page<ConversionHistoryResponse>> getConversionByDate(
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(exchangeService.getConversionsByDate(date, PageRequest.of(page, size)));
    }

}
