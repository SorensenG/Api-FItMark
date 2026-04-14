package com.Sorensen.FitMark.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Código é obrigatório")
        @Size(min = 6, max = 6, message = "Código deve ter 6 dígitos")
        String code,

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 6, max = 128, message = "Senha deve ter entre 6 e 128 caracteres")
        String newPassword
) {}
