package com.example.java101.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank @Email @Size(min = 5, max = 255) String email,
        @NotBlank @Size(min = 8, max = 128) String password) {}
