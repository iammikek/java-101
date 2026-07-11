package com.example.java101.dto;

import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(@Size(min = 1, max = 100) String name, String description) {}
