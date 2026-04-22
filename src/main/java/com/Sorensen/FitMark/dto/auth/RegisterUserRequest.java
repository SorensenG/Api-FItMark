package com.Sorensen.FitMark.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max = 120, message = "Username must have between 3 and 120 characters")
        String username,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email")
        @Size(max = 255, message = "Email too long")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 128, message = "Password must have between 8 and 128 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
                message = "Password must include uppercase, lowercase, number and special character"
        )
        String password
) {}
