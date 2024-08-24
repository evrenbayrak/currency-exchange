package org.project.currencyexchange.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.project.currencyexchange.model.dto.ExchangeCurrency;

import java.math.BigDecimal;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversion_history")
public class ConversionHistory extends BaseAudit {

    @Id
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private String transactionId;

    @Column(name = "base_currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExchangeCurrency baseCurrency;

    @Column(name = "target_currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExchangeCurrency targetCurrency;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "converted_amount", nullable = false)
    private BigDecimal convertedAmount;

}
