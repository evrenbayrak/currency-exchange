package org.project.currencyexchange.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.currencyexchange.model.dto.*;
import org.project.currencyexchange.model.entity.ConversionHistory;
import org.project.currencyexchange.repository.ConversionHistoryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {

    @Mock
    private ExternalExchangeApi mockExternalApi;
    @Mock
    private ConversionHistoryRepository mockRepository;
    @InjectMocks
    private ExchangeService exchangeService;

    @Test
    void shouldReturnExpectedExchangeRate() {
        // Given
        ExchangeCurrency baseCurrency = ExchangeCurrency.USD;
        ExchangeCurrency targetCurrency = ExchangeCurrency.EUR;
        String mapKey = baseCurrency.name() + targetCurrency.name();
        BigDecimal expectedRate = BigDecimal.valueOf(0.9);

        // When
        when(mockExternalApi.getExchangeRates(baseCurrency.name()))
                .thenReturn(Map.of(mapKey, expectedRate));

        ExchangeRateDto result = exchangeService.getExchangeRate(baseCurrency.name(), targetCurrency.name());

        // Then
        assertNotNull(result);
        assertEquals(expectedRate.setScale(3, RoundingMode.HALF_UP), result.rate());
    }

    @Test
    void shouldSuccessfullyConvertGivenAmountBetweenCurrencies() {
        // Given
        ExchangeCurrency baseCurrency = ExchangeCurrency.USD;
        ExchangeCurrency targetCurrency = ExchangeCurrency.EUR;
        String mapKey = baseCurrency.name() + targetCurrency.name();
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal exchangeRate = BigDecimal.valueOf(0.75);
        BigDecimal expectedConversionAmount = BigDecimal.valueOf(75);
        ConversionRequest request = new ConversionRequest(baseCurrency, targetCurrency, amount);

        // When
        when(mockExternalApi.getExchangeRates(baseCurrency.name()))
                .thenReturn(Map.of(mapKey, exchangeRate));
        when(mockRepository.save(Mockito.any(ConversionHistory.class)))
                .thenReturn(new ConversionHistory());

        ConversionResponse response = exchangeService.convertAmount(request);

        // Then
        assertNotNull(response.transactionId());
        assertEquals(expectedConversionAmount.setScale(3, RoundingMode.HALF_UP),
                response.conversionAmount());
    }

    @Test
    void shouldSuccessfullyGetConversionByTransactionId() {
        // Given
        UUID transactionId = UUID.randomUUID();
        ConversionHistory history = ConversionHistory.builder()
                .transactionId(transactionId.toString())
                .baseCurrency(ExchangeCurrency.USD)
                .targetCurrency(ExchangeCurrency.EUR)
                .amount(BigDecimal.valueOf(100))
                .convertedAmount(BigDecimal.valueOf(85)).build();

        when(mockRepository.findById(transactionId.toString()))
                .thenReturn(Optional.of(history));

        // When
        ConversionHistoryResponse response = exchangeService.getConversionByTransactionId(transactionId);

        //Then
        assertNotNull(response);
        assertAll("Response data should match with inserted one.",
                () -> assertEquals(history.getTransactionId(), response.transactionId()),
                () -> assertEquals(history.getAmount(), response.baseAmount()),
                () -> assertEquals(history.getBaseCurrency(), response.baseCurrency()),
                () -> assertEquals(history.getTargetCurrency(), response.targetCurrency()),
                () -> assertEquals(history.getConvertedAmount(), response.conversionAmount()),
                () -> assertEquals(history.getCreatedAt(), response.conversionDate())
        );
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenGetConversionByTransactionId() {
        // Given
        UUID transactionId = UUID.randomUUID();

        // When
        when(mockRepository.findById(transactionId.toString()))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> exchangeService.getConversionByTransactionId(transactionId));
    }

}
