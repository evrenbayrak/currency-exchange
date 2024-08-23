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

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExternalExchangeApi externalExchangeApi;
    private final ConversionHistoryRepository conversionHistoryRepository;

    public ExchangeRateDto getExchangeRate(String baseCurrency, String targetCurrency) {
        double exchangeRate = retrieveExchangeRate(baseCurrency, targetCurrency);
        return new ExchangeRateDto(exchangeRate);
    }

    public ConversionResponse convertAmount(ConversionRequest conversionRequest) {
        double exchangeRate = retrieveExchangeRate(conversionRequest.baseCurrency().name(),
                conversionRequest.targetCurrency().name());
        double convertedAmount = exchangeRate * conversionRequest.amount();
        String transactionId = UUID.randomUUID().toString();
        ConversionResponse response = new ConversionResponse(transactionId, convertedAmount);
        saveResult(conversionRequest, response);
        return response;
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

    private double retrieveExchangeRate(String baseCurrency, String targetCurrency) {
        return externalExchangeApi.getExchangeRates(baseCurrency).get(baseCurrency + targetCurrency);
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
