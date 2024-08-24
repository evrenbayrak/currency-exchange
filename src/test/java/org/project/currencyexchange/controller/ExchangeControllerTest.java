package org.project.currencyexchange.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.project.currencyexchange.exception.ExchangeErrorCode;
import org.project.currencyexchange.model.dto.*;
import org.project.currencyexchange.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ExchangeControllerTest {

    private static final BigDecimal fakeDecimal = BigDecimal.valueOf(1.2);

    @MockBean
    ExchangeService mockedExchangeService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void shouldSuccessIfServiceReturnExchangeRate() throws Exception {
        //Given
        ExchangeRateDto fakeResponse = new ExchangeRateDto(fakeDecimal);

        //When
        doReturn(fakeResponse).when(mockedExchangeService).getExchangeRate("USD", "EUR");

        //Then
        mockMvc.perform(MockMvcRequestBuilders.get(contextPath + "/api/v1/exchange-rate/USD/EUR")
                        .contextPath(contextPath))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.rate").value(fakeDecimal));
    }

    @Test
    void shouldFailWithHttp400IfExchangeRateCurrenciesNotValid() throws Exception {

        //When-Then
        mockMvc.perform(MockMvcRequestBuilders.get(contextPath + "/api/v1/exchange-rate/XYZ/XYZ")
                        .contextPath(contextPath))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ExchangeErrorCode.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ExchangeErrorCode.BAD_REQUEST.getMessage()))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.detail.invalidFieldInfos").isArray());

    }

    @Test
    void shouldSuccessWhenConvertAmount() throws Exception {
        // Given
        ConversionResponse fakeResponse = new ConversionResponse("fakeTransactionId", fakeDecimal);
        ConversionRequest request = new ConversionRequest(ExchangeCurrency.USD, ExchangeCurrency.EUR, fakeDecimal);

        // When
        doReturn(fakeResponse).when(mockedExchangeService).convertAmount(Mockito.any(ConversionRequest.class));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(contextPath + "/api/v1/conversion")
                        .contextPath(contextPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.transactionId").value("fakeTransactionId"))
                .andExpect(jsonPath("$.conversionAmount").value(fakeDecimal));
    }

    @Test
    void shouldFailWithHttp400WhenConvertAmountWithInvalidCurrency() throws Exception {
        // Given
        ConversionRequest request = new ConversionRequest(ExchangeCurrency.USD, null, fakeDecimal);

        // When-Then
        mockMvc.perform(MockMvcRequestBuilders.post(contextPath + "/api/v1/conversion")
                        .contextPath(contextPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ExchangeErrorCode.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ExchangeErrorCode.BAD_REQUEST.getMessage()))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.detail.invalidFieldInfos").isArray());
    }

    @Test
    void shouldSuccessWhenGetConversionHistoryByExistedTransactionId() throws Exception {
        // Given
        String fakeTransactionId = UUID.randomUUID().toString();
        ConversionHistoryResponse fakeResponse = new ConversionHistoryResponse(
                fakeTransactionId, ExchangeCurrency.USD, ExchangeCurrency.EUR, fakeDecimal, fakeDecimal, LocalDateTime.now());

        // When
        doReturn(fakeResponse).when(mockedExchangeService).getConversionByTransactionId(UUID.fromString(fakeTransactionId));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(contextPath + "/api/v1/conversion-history/" + fakeTransactionId)
                        .contextPath(contextPath))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.transactionId").value(fakeResponse.transactionId()))
                .andExpect(jsonPath("$.baseCurrency").value(fakeResponse.baseCurrency().name()))
                .andExpect(jsonPath("$.targetCurrency").value(fakeResponse.targetCurrency().name()))
                .andExpect(jsonPath("$.baseAmount").value(fakeResponse.baseAmount()))
                .andExpect(jsonPath("$.conversionAmount").value(fakeResponse.conversionAmount()))
                .andExpect(jsonPath("$.conversionDate").value(fakeResponse.conversionDate().toString()));
    }

    @Test
    void shouldFailWithHttp404WhenTransactionIdNotFound() throws Exception {
        // When
        String nonexistentFakeTransactionId = UUID.randomUUID().toString();
        when(mockedExchangeService.getConversionByTransactionId(Mockito.any(UUID.class)))
                .thenThrow(new EntityNotFoundException("entity not found"));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(contextPath + "/api/v1/conversion-history/" + nonexistentFakeTransactionId)
                        .contextPath(contextPath))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ExchangeErrorCode.ENTITY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.errorMessage").value(ExchangeErrorCode.ENTITY_NOT_FOUND.getMessage()));
    }

    @Test
    void shouldSuccessWhenGetConversionHistoryByDate() throws Exception {
        // Given
        BigDecimal conversionAmount1 = BigDecimal.valueOf(20);
        BigDecimal conversionAmount2 = BigDecimal.valueOf(10);
        String fakeTransactionId1 = UUID.randomUUID().toString();
        String fakeTransactionId2 = UUID.randomUUID().toString();
        ConversionHistoryResponse fakeResponse1 = new ConversionHistoryResponse(
                fakeTransactionId1, ExchangeCurrency.USD, ExchangeCurrency.EUR, fakeDecimal,
                conversionAmount1, LocalDateTime.now());
        ConversionHistoryResponse fakeResponse2 = new ConversionHistoryResponse(
                fakeTransactionId2, ExchangeCurrency.USD, ExchangeCurrency.EUR, fakeDecimal,
                conversionAmount2, LocalDateTime.now());

        //When
        doReturn(new PageImpl<>(List.of(fakeResponse1, fakeResponse2)))
                .when(mockedExchangeService).getConversionsByDate(Mockito.any(LocalDate.class),
                        Mockito.any(PageRequest.class));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(contextPath + "/api/v1/conversion-history")
                        .param("date", "2023-08-24")
                        .contextPath(contextPath))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].transactionId").value(fakeTransactionId1))
                .andExpect(jsonPath("$.content[1].transactionId").value(fakeTransactionId2));
    }


}
