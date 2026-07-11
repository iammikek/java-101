package com.example.java101.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ItemCreateRequest(
        @NotBlank @Size(min = 1, max = 255) String name,
        String description,
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal price,
        Long categoryId) {}
