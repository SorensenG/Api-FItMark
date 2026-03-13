package com.Sorensen.FitMark.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        int status,
        String error,
        String message,
        List<String> details,
        OffsetDateTime timestamp
) {
    public ApiError(int status, String error, String message) {
        this(status, error, message, null, OffsetDateTime.now());
    }

    public ApiError(int status, String error, String message, List<String> details) {
        this(status, error, message, details, OffsetDateTime.now());
    }
}
