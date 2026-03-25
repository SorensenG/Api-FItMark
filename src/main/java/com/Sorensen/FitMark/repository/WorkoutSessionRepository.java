package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
    Optional<List<WorkoutSession>> findByUserIdOrderByWorkoutDateDesc(UUID id);
}
