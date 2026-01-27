package com.Sorensen.FitMark.dto.auth;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(@NotEmpty(message = "email cannot be empty") String email,
                           @NotEmpty(message = "password cannot be empty") String password) {
}
