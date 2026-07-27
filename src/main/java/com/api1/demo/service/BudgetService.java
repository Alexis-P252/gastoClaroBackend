package com.api1.demo.service;


import com.api1.demo.entity.Budget;
import com.api1.demo.entity.Category;
import com.api1.demo.entity.Transaction;
import com.api1.demo.entity.User;
import com.api1.demo.exception.BudgetExceededException;
import com.api1.demo.exception.ResourceNotFoundException;
import com.api1.demo.repository.BudgetRepository;
import com.api1.demo.repository.TransactionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository,
                         CategoryService categoryService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    public List<Budget> listForMonth(UUID userId, String month) {
        return budgetRepository.findByUserIdAndMonth(userId, month);
    }

    public Budget setBudget(User user, UUID categoryId, BigDecimal monthlyLimit, String month) {
        if (monthlyLimit == null || monthlyLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El presupuesto debe ser mayor a cero");
        }

        Category category = categoryService.getOwned(categoryId, user.getId());

        // Si ya hay presupuesto para esa categoría/mes, lo actualiza en vez de duplicar
        Budget budget = budgetRepository
                .findByUserIdAndCategoryIdAndMonth(user.getId(), categoryId, month)
                .orElseGet(Budget::new);

        // Regla de negocio: no dejamos bajar el límite por debajo de lo ya gastado
        // en ese mes, para que el presupuesto no quede "roto" desde el día 1.
        BigDecimal alreadySpent = totalSpent(user.getId(), categoryId, month);
        if (monthlyLimit.compareTo(alreadySpent) < 0) {
            throw new BudgetExceededException(
                    "El límite (" + monthlyLimit + ") es menor a lo ya gastado este mes (" + alreadySpent + ")");
        }

        budget.setUser(user);
        budget.setCategory(category);
        budget.setMonthlyLimit(monthlyLimit);
        budget.setMonth(month);

        return budgetRepository.save(budget);
    }

    public BigDecimal totalSpent(UUID userId, UUID categoryId, String month) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<Transaction> transactions = transactionRepository
                .findByUserIdAndCategoryIdAndDateBetween(userId, categoryId, from, to, Pageable.unpaged())
                .getContent();

        return transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}