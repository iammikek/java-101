package com.example.java101.dto;

import java.util.List;

public record PaginatedResponse<T>(List<T> items, long total, int skip, int limit) {}
