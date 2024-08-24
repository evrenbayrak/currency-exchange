package org.project.currencyexchange.repository;

import org.junit.jupiter.api.Test;
import org.project.currencyexchange.model.dto.ExchangeCurrency;
import org.project.currencyexchange.model.entity.ConversionHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class ConversionHistoryRepositoryDataJpaIT {

    @Autowired
    ConversionHistoryRepository repository;

    @TestConfiguration(proxyBeanMethods = false)
    public static class TestAuditConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> Optional.of("h2-user");
        }
    }

    @Test
    void shouldInsertSampleConversionHistoryToTable() {
        // Given
        ExchangeCurrency baseCurrency = ExchangeCurrency.ARS;
        ExchangeCurrency targetCurrency = ExchangeCurrency.CAD;
        String transactionId = UUID.randomUUID().toString();
        BigDecimal amount = BigDecimal.valueOf(20);
        BigDecimal conversionAmount = BigDecimal.valueOf(100);

        ConversionHistory conversionHistory = ConversionHistory.builder()
                .targetCurrency(targetCurrency)
                .baseCurrency(baseCurrency)
                .transactionId(transactionId)
                .amount(amount)
                .convertedAmount(conversionAmount).build();

        // When
        ConversionHistory insertData = repository.saveAndFlush(conversionHistory);
        Optional<ConversionHistory> requestedData = repository.findById(transactionId);

        // Then
        assertTrue(requestedData.isPresent());
        assertEquals(insertData, requestedData.get());
        assertNotNull(requestedData.get().getCreatedAt());

    }

    @Test
    void shouldFindByCreatedAtBetweenWithPagination() {
        //Given
        LocalDateTime startOfDay = LocalDateTime.of(2024, 8, 24, 0, 0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // When
        PageRequest firstPageRequest = PageRequest.of(0, 2);
        Page<ConversionHistory> firstPage = repository.findByCreatedAtBetween(startOfDay, endOfDay, firstPageRequest);
        PageRequest secondPageRequest = PageRequest.of(1, 2);
        Page<ConversionHistory> secondPage = repository.findByCreatedAtBetween(startOfDay, endOfDay, secondPageRequest);

        // Then
        assertEquals(2, firstPage.getContent().size(), "First page must contain 2 records");
        assertEquals(1, secondPage.getContent().size(), "Second page must contain 1 record");
        assertNotEquals(firstPage.getContent().get(0).getTransactionId(), secondPage.getContent().get(0).getTransactionId(),
                "First and second pages should not contain the same records");

    }

}
