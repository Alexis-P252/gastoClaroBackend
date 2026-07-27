package com.api1.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String categoryName,
        BigDecimal amount,
        String description,
        LocalDate date,
        String type,
        boolean recurring,
        List<String> tags
) {}
