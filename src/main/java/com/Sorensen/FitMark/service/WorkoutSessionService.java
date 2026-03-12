package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.dto.exercise.ExerciseSessionResponse;
import com.Sorensen.FitMark.dto.workout.StartWorkOutSessionResponse;
import com.Sorensen.FitMark.entity.WorkoutSession;
import com.Sorensen.FitMark.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WorkoutSessionService {
    final private EntityFinder finder;
   final private WorkoutSessionRepository repository;

    public WorkoutSessionService(EntityFinder finder, WorkoutSessionRepository repository) {
        this.finder = finder;
        this.repository = repository;
    }

    public StartWorkOutSessionResponse startWorkoutSession(UUID userId, UUID splitId,UUID workoutID) {

        var workoutOpt = finder.workout(workoutID);
        var userOpt = finder.user(userId);

        if (workoutOpt.getUser() == null || !workoutOpt.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Workout does not belong to user");
        }

        if (workoutOpt.getSplit() == null || !workoutOpt.getSplit().getId().equals(splitId)) {
            throw new IllegalArgumentException("Workout does not belong to split");
        }

        WorkoutSession workoutSession = WorkoutSession.builder()
                .user(userOpt)
                .workout(workoutOpt)
                .workoutDate(OffsetDateTime.now())
                .completed(false)
                .build();

        workoutSession = repository.save(workoutSession);

        List <ExerciseSessionResponse> exercises = workoutOpt.getExercises().
                stream().map(e-> new ExerciseSessionResponse(  e.getId(),
                        e.getName(),
                        e.getSets(),
                        e.getLastTopSetReps(),
                        e.getWeight(),
                        e.getPosition())).toList();


        return new StartWorkOutSessionResponse(
                workoutSession.getId(),
                workoutOpt.getId(),
                workoutOpt.getTitle(),
                workoutSession.getWorkoutDate(),
                workoutSession.getCompleted(),
                exercises
        );
    }


}
