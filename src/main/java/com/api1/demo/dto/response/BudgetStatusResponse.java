package com.api1.demo.dto.response;

import java.math.BigDecimal;

public record BudgetStatusResponse(
        String categoryName,
        BigDecimal spent,
        BigDecimal limit,
        double percentageUsed,
        boolean exceeded
) {}
