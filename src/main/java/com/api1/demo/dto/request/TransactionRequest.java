package com.api1.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TransactionRequest(

        @NotNull(message = "La categoría es obligatoria")
        UUID categoryId,

        @NotNull
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal amount,

        String description,

        @NotNull
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate date,

        @NotNull
        @Pattern(regexp = "INCOME|EXPENSE", message = "type debe ser INCOME o EXPENSE")
        String type,

        boolean recurring,

        // Opcional: puede venir vacía o nula si la transacción no tiene tags
        List<UUID> tagIds
) {}