package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.RefreshToken;
import com.Sorensen.FitMark.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);
}
