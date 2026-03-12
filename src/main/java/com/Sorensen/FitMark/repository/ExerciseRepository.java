package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    Optional<Exercise> findTopByWorkoutIdOrderByPositionDesc(UUID workoutId);




}
