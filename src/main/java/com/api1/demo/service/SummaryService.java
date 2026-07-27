package com.api1.demo.service;


import com.api1.demo.entity.Budget;
import com.api1.demo.entity.Transaction;
import com.api1.demo.repository.TransactionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;

    public SummaryService(TransactionRepository transactionRepository, BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
    }

    // --- Resumen de un mes puntual: ingresos, gastos, balance, desglose por categoría ---
    public MonthlySummary getMonthlySummary(UUID userId, String month) {

        // Convertir el mes a rango de fechas
        YearMonth ym = YearMonth.parse(month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        //Obtengo las transacciones del mes
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndDateBetween(userId, from, to, Pageable.unpaged())
                .getContent();


        BigDecimal income = sumByType(transactions, "INCOME");
        BigDecimal expense = sumByType(transactions, "EXPENSE");

        // Desglose por categoria
        Map<String, BigDecimal> byCategory = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

        return new MonthlySummary(month, income, expense, income.subtract(expense), byCategory);
    }

    // --- Tendencia de los últimos N meses (por defecto 6), para ver la evolución ---
    public List<MonthlySummary> getTrend(UUID userId, int monthsBack) {
        List<MonthlySummary> trend = new ArrayList<>();
        YearMonth current = YearMonth.now();

        for (int i = monthsBack - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            trend.add(getMonthlySummary(userId, ym.toString()));
        }
        return trend;
    }

    // --- Estado de cada presupuesto del mes: cuánto lleva gastado vs. su límite ---
    public List<BudgetStatus> getBudgetStatus(UUID userId, String month) {
        List<Budget> budgets = budgetService.listForMonth(userId, month);

        return budgets.stream()
                .map(budget -> {
                    BigDecimal spent = budgetService.totalSpent(
                            userId, budget.getCategory().getId(), month);
                    BigDecimal limit = budget.getMonthlyLimit();
                    double percentage = limit.compareTo(BigDecimal.ZERO) == 0
                            ? 0
                            : spent.divide(limit, 4, java.math.RoundingMode.HALF_UP).doubleValue() * 100;

                    return new BudgetStatus(
                            budget.getCategory().getName(), spent, limit,
                            percentage, spent.compareTo(limit) > 0);
                })
                .collect(Collectors.toList());
    }

    private BigDecimal sumByType(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> type.equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Resultados internos del service. En la capa de Controller se van a mapear
    // a sus DTOs de response correspondientes (MonthlySummaryResponse, etc).
    public record MonthlySummary(
            String month, BigDecimal income, BigDecimal expense,
            BigDecimal balance, Map<String, BigDecimal> byCategory) {}

    public record BudgetStatus(
            String categoryName, BigDecimal spent, BigDecimal limit,
            double percentageUsed, boolean exceeded) {}
}