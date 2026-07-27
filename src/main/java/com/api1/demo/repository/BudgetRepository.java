package com.api1.demo.repository;

import com.api1.demo.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    List<Budget> findByUserIdAndMonth(UUID userId, String month);

    Optional<Budget> findByUserIdAndCategoryIdAndMonth(UUID userId, UUID categoryId, String month);
}
