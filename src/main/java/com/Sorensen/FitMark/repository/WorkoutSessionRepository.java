package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
    Optional<List<WorkoutSession>> findByUserIdOrderByWorkoutDateDesc(UUID id);


    @Query("""
    SELECT CASE WHEN (w.completed = false AND w.abandoned = false) THEN true ELSE false END
    FROM WorkoutSession w
    WHERE w.user.id = :userId
    ORDER BY w.workoutDate DESC
    LIMIT 1
""")
    Optional<Boolean> hasActiveSession(@Param("userId") UUID userId);
}