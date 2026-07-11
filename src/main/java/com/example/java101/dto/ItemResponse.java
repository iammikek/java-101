package com.example.java101.dto;

import java.math.BigDecimal;

public record ItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        CategoryResponse category) {}
