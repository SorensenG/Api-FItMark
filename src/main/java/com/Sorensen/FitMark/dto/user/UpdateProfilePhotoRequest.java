package com.Sorensen.FitMark.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfilePhotoRequest(@NotBlank String profilePhotoUrl) {
}
