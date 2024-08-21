package org.project.currencyexchange.controller;

import lombok.RequiredArgsConstructor;
import org.project.currencyexchange.model.dto.ExchangeCurrency;
import org.project.currencyexchange.model.dto.ExchangeRateDto;
import org.project.currencyexchange.service.ExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
