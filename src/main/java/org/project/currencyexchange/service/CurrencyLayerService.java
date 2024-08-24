package org.project.currencyexchange.service;

import lombok.extern.slf4j.Slf4j;
import org.project.currencyexchange.exception.ExchangeCurrencyException;
import org.project.currencyexchange.exception.ExchangeErrorCode;
import org.project.currencyexchange.model.api.CurrencyLayerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CurrencyLayerService implements ExternalExchangeApi {

    private final RestClient restClient;
    private final String apiUrl;
    private final String accessKey;

    public CurrencyLayerService(RestClient restClient,
                                @Value("${external.api.url}") String apiUrl,
                                @Value("${external.api.access_key}") String accessKey) {
        this.restClient = restClient;
        this.apiUrl = apiUrl;
        this.accessKey = accessKey;
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "#baseCurrency", unless = "#result == null")
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency) {
        log.info("Calling external api to retrieve exchange rates for currency: {}", baseCurrency);
        String url = String.format("%s/live?access_key=%s&source=%s&format=1",
                apiUrl, accessKey, baseCurrency);
        try {
            Optional<CurrencyLayerResponse> optionalResponse = Optional.ofNullable(
                    restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(CurrencyLayerResponse.class));

            return validateAndRetrieveRates(optionalResponse);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                throw new ExchangeCurrencyException(ExchangeErrorCode.EXTERNAL_API_ERROR, e);
            }
            throw new ExchangeCurrencyException(ExchangeErrorCode.INVALID_API_REQUEST, e);
        } catch (ResourceAccessException e) { //timeout exception
            throw new ExchangeCurrencyException(ExchangeErrorCode.EXTERNAL_API_TIMEOUT, e);
        } catch (RestClientException e) {
            throw new ExchangeCurrencyException(ExchangeErrorCode.EXTERNAL_API_ERROR, e);
        }
    }

    private Map<String, BigDecimal> validateAndRetrieveRates(Optional<CurrencyLayerResponse> optionalResponse){
        CurrencyLayerResponse response = optionalResponse
                .orElseThrow(() -> new ExchangeCurrencyException(ExchangeErrorCode.EXTERNAL_API_ERROR));

        if (!response.isSuccess()) {
            log.error("External api request failed with error: {}", response.getError().getInfo());
            throw new ExchangeCurrencyException(checkApiException(response.getError().getCode()));
        }

        return response.getQuotes();
    }

    private ExchangeErrorCode checkApiException(int code){
        return switch (code){
            case 101,102 -> ExchangeErrorCode.INVALID_API_CREDENTIALS;
            case 104 -> ExchangeErrorCode.API_LIMIT_EXCEEDED;
            case 201, 202 -> ExchangeErrorCode.INVALID_API_REQUEST;
            default -> ExchangeErrorCode.EXTERNAL_API_ERROR;
        };
    }

}
