package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {
}
