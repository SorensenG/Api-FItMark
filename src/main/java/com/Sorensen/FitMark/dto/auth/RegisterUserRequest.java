package com.Sorensen.FitMark.dto.auth;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(@NotEmpty(message = "username cannto be empty") String username,
                                  @NotEmpty(message = "email cannot be empty") String email,
                                  @NotEmpty(message = "password cannot be empty") String password) {
}
