package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {

    @Modifying
    void deleteByIdAndUserId(UUID workoutId, UUID userId);

    boolean existsByIdAndUserId(UUID workoutId, UUID userId);

    Optional<Workout> findTopByUserIdAndSplitIdOrderByPositionDesc(UUID userId, UUID splitId);

    List<Workout> findByTitle(String title);

    List<Workout> findByUserId(UUID userId);

    Optional<Workout> findByIdAndUserId(UUID workoutId, UUID userId);
}



