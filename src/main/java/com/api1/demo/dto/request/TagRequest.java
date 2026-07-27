package com.api1.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record TagRequest(
        @NotBlank(message = "El nombre de la tag es obligatorio")
        String name
) { }
