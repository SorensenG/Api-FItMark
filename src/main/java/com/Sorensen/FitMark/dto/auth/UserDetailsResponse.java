package com.Sorensen.FitMark.dto.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDetailsResponse(UUID UserID, String username, String email, OffsetDateTime createdAt) {
}
