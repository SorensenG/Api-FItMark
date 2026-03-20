package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.SetLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SetLogRepository extends JpaRepository<SetLog, UUID> {
    List<SetLog> findByExerciseIdOrderByCreatedAtDesc(UUID exerciseId);


}
