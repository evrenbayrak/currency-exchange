package org.project.currencyexchange.service;

import org.project.currencyexchange.model.api.CurrencyLayerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

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
    @Cacheable(value = "exchangeRates", key = "#baseCurrency", unless = "#result == null")
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency) {
        String url = String.format("%s/live?access_key=%s&source=%s&format=1",
                apiUrl, accessKey, baseCurrency);

        ResponseEntity<CurrencyLayerResponse> responseEntity = restTemplate.getForEntity(url, CurrencyLayerResponse.class);
        CurrencyLayerResponse response = responseEntity.getBody();
        System.out.println("CurrencyLayerService.callExternalApi returned from api");
        return response.getQuotes();
    }

}
