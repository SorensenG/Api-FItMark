package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    Optional<Exercise> findTopByWorkoutIdOrderByPositionDesc(UUID workoutId);

@Modifying
@Query("UPDATE Exercise e SET e.position = e.position - 1 WHERE e.workout.id = :workoutId AND e.position > :position")
void decrementPositionsAfter(@Param("workoutId") UUID workoutId, @Param("position") Integer position);                                                                        }
