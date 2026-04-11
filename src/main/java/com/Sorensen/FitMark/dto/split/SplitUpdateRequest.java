package com.Sorensen.FitMark.dto.split;

import jakarta.validation.constraints.NotBlank;

public record SplitUpdateRequest(
        @NotBlank String title
) {
}
