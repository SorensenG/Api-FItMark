package com.Sorensen.FitMark.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDetailsResponse(String username, UUID id, String email, OffsetDateTime createdAt) {
}
