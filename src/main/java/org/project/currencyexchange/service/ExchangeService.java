package org.project.currencyexchange.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.project.currencyexchange.model.dto.ConversionHistoryResponse;
import org.project.currencyexchange.model.dto.ConversionRequest;
import org.project.currencyexchange.model.dto.ConversionResponse;
import org.project.currencyexchange.model.dto.ExchangeRateDto;
import org.project.currencyexchange.model.entity.ConversionHistory;
import org.project.currencyexchange.repository.ConversionHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExternalExchangeApi externalExchangeApi;
    private final ConversionHistoryRepository conversionHistoryRepository;

    public ExchangeRateDto getExchangeRate(String baseCurrency, String targetCurrency) {
        BigDecimal exchangeRate = retrieveExchangeRate(baseCurrency, targetCurrency);
        return new ExchangeRateDto(exchangeRate);
    }

    public ConversionResponse convertAmount(ConversionRequest conversionRequest) {
        BigDecimal exchangeRate = retrieveExchangeRate(conversionRequest.baseCurrency().name(),
                conversionRequest.targetCurrency().name());
        BigDecimal convertedAmount = exchangeRate.multiply(conversionRequest.amount());
        String transactionId = UUID.randomUUID().toString();
        ConversionResponse response = new ConversionResponse(transactionId, convertedAmount);
        saveResult(conversionRequest, response);
        return response;
    }


    public ConversionHistoryResponse getConversionByTransactionId(UUID transactionId) {
        return conversionHistoryRepository.findById(transactionId.toString())
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("conversion not found by given id"));
    }


    public Page<ConversionHistoryResponse> getConversionsByDate(LocalDate date, Pageable pageable) {
        return conversionHistoryRepository.findByCreatedAtBetween(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(),
                pageable)
                .map(this::convertToDto);

    }

    private BigDecimal retrieveExchangeRate(String baseCurrency, String targetCurrency) {
        BigDecimal exchangeRate = externalExchangeApi.getExchangeRates(baseCurrency).get(baseCurrency + targetCurrency);
        return exchangeRate.setScale(3, RoundingMode.HALF_UP);
    }

    private void saveResult(ConversionRequest request, ConversionResponse response) {
        ConversionHistory conversionHistory = ConversionHistory.builder()
                .transactionId(response.transactionId())
                .convertedAmount(response.conversionAmount())
                .amount(request.amount())
                .baseCurrency(request.baseCurrency())
                .targetCurrency(request.targetCurrency())
                .build();
        conversionHistoryRepository.save(conversionHistory);
    }

    private ConversionHistoryResponse convertToDto(ConversionHistory history) {
        return new ConversionHistoryResponse(
                history.getTransactionId(),
                history.getBaseCurrency(),
                history.getTargetCurrency(),
                history.getAmount(), history.getConvertedAmount(),
                history.getCreatedAt()
        );
    }
}
