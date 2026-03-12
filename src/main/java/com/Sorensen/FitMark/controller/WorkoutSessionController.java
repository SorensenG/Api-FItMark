package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.workout.StartWorkOutSessionResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.WorkoutSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/splits/{splitId}/workouts/{workoutId}")


public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @PostMapping("/workoutsession-start")
    public ResponseEntity<StartWorkOutSessionResponse> startWorkoutSession(@AuthenticationPrincipal User user,
                                                                           @PathVariable UUID workoutId,
                                                                           @PathVariable UUID splitId) {

        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var id = user.getId();

        var sessionStartedOpt = workoutSessionService.startWorkoutSession(id, splitId,workoutId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new StartWorkOutSessionResponse(sessionStartedOpt.sessionId(), sessionStartedOpt.workoutId(), sessionStartedOpt.workoutTitle(), sessionStartedOpt.startedAt(), sessionStartedOpt.completed(), sessionStartedOpt.workoutExercises()));
    }



}
