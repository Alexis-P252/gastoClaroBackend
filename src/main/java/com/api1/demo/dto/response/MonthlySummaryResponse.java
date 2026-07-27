package com.api1.demo.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlySummaryResponse(
        String month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance,
        Map<String, BigDecimal> byCategory
) {}
