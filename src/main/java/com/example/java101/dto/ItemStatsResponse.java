package com.example.java101.dto;

import java.math.BigDecimal;
import java.util.List;

public record ItemStatsResponse(
        long totalItems,
        BigDecimal averagePrice,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        long uncategorizedCount,
        List<CategoryItemStats> byCategory) {}
