package org.project.currencyexchange.repository;

import org.project.currencyexchange.model.entity.ConversionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConversionHistoryRepository extends JpaRepository<ConversionHistory, String> {

    Page<ConversionHistory> findByCreatedAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

}
