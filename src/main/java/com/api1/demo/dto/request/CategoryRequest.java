package com.api1.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest (

    @NotBlank(message = "El nombre es obligatorio")
    String name,

    @NotBlank
    @Pattern(regexp = "INCOME|EXPENSE", message = "type debe ser INCOME o EXPENSE")
    String type
) {}
