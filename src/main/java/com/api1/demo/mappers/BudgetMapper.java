package com.api1.demo.mappers;

import com.api1.demo.dto.response.BudgetResponse;
import com.api1.demo.entity.Budget;

import java.util.List;

public class BudgetMapper {

    private BudgetMapper() {}

    public static BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getName(),
                budget.getMonthlyLimit(),
                budget.getMonth()
        );
    }

    public static List<BudgetResponse> toResponseList(List<Budget> budgets) {
        return budgets.stream().map(BudgetMapper::toResponse).toList();
    }

    // Mismo caso que Transaction: crear/actualizar un Budget necesita resolver
    // la Category (con ownership) y comparar contra lo ya gastado, así que
    // ese armado queda en BudgetService, no acá.
}
