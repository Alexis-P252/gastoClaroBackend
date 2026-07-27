package com.api1.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetRequest(

        @NotNull(message = "La categoría es obligatoria")
        UUID categoryId,

        @NotNull
        @Positive(message = "El límite debe ser mayor a cero")
        BigDecimal monthlyLimit,

        // Formato "yyyy-MM", ej. "2026-07"
        @NotBlank
        @Pattern(regexp = "\\d{4}-\\d{2}", message = "month debe tener formato yyyy-MM")
        String month
) {}
