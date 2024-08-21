package org.project.currencyexchange.service;

import org.project.currencyexchange.model.api.CurrencyLayerResponse;
import org.project.currencyexchange.model.dto.ExchangeRateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyLayerService implements ExternalExchangeApi {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String accessKey;

    public CurrencyLayerService(RestTemplate restTemplate,
                                @Value("${external.api.url}") String apiUrl,
                                @Value("${external.api.access_key}") String accessKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.accessKey = accessKey;
    }


    @Override
    public ExchangeRateDto getExchangeRate(String baseCurrency, String targetCurrency) {
        String url = String.format("%s/live?access_key=%s&source=%s&currencies=%s&format=1",
                apiUrl, accessKey, baseCurrency, targetCurrency);

        ResponseEntity<CurrencyLayerResponse> responseEntity = restTemplate.getForEntity(url, CurrencyLayerResponse.class);
        CurrencyLayerResponse response = responseEntity.getBody();

        if (response != null && response.isSuccess()) {
            double exchangeRate = response.getQuotes().get(baseCurrency + targetCurrency);
            return new ExchangeRateDto(exchangeRate);
        } else {
            throw new RuntimeException("Failed to fetch exchange rates from CurrencyLayer API");
        }
    }


}
