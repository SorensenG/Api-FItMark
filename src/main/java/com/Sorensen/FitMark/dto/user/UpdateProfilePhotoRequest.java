package com.Sorensen.FitMark.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record UpdateProfilePhotoRequest(@NotEmpty String profilePhotoUrl) {
}
