package com.api1.demo.repository;

import com.api1.demo.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    // Base del filtro de fechas/categoría que va a usar el endpoint GET /transactions
    Page<Transaction> findByUserIdAndDateBetween(
            UUID userId, LocalDate from, LocalDate to, Pageable pageable);

    Page<Transaction> findByUserIdAndCategoryIdAndDateBetween(
            UUID userId, UUID categoryId, LocalDate from, LocalDate to, Pageable pageable);

    // Para el job que genera las transacciones recurrentes cada mes
    List<Transaction> findByUserIdAndRecurringTrue(UUID userId);
}
