package com.api1.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Email(message = "El email no es válido")
        String email,

        @NotBlank
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password
) {}
