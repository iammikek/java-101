package com.example.java101.dto;

import java.math.BigDecimal;

public record CategoryItemStats(
        Long categoryId, String categoryName, long itemCount, BigDecimal averagePrice) {}
