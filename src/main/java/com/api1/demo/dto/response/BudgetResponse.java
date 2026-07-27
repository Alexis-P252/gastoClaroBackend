package com.api1.demo.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        String categoryName,
        BigDecimal monthlyLimit,
        String month
) {}
