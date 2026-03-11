package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.dto.workout.StartWorkOutSessionResponse;
import com.Sorensen.FitMark.entity.WorkoutSession;
import com.Sorensen.FitMark.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class WorkoutSessionService {
    private EntityFinder finder;
    private WorkoutSessionRepository repository;

    public WorkoutSessionService(EntityFinder finder, WorkoutSessionRepository repository) {
        this.finder = finder;
        this.repository = repository;
    }

    public StartWorkOutSessionResponse startWorkoutSession(UUID userid, UUID workoutID) {

        var workoutOpt = finder.workout(workoutID);
        var userOpt = finder.user(userid);

        WorkoutSession workoutSession = WorkoutSession.builder()
                .user(userOpt)
                .workout(workoutOpt)
                .workoutDate(OffsetDateTime.now())
                .completed(false)
                .build();

        workoutSession = repository.save(workoutSession);

        return new StartWorkOutSessionResponse(
                workoutSession.getId(),
                workoutOpt.getId(),
                workoutOpt.getTitle(),
                workoutSession.getWorkoutDate(),
                workoutSession.getCompleted(),
                workoutOpt.getExercises()
        );
    }


}
