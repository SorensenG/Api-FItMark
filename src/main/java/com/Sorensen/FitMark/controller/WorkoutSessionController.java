package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.workout.*;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.WorkoutSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/splits/{splitId}/workouts/{workoutId}")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @PostMapping("/workoutsession-start")
    public ResponseEntity<StartWorkOutSessionResponse> startWorkoutSession(
            @AuthenticationPrincipal User user,
            @PathVariable UUID workoutId,
            @PathVariable UUID splitId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var response = workoutSessionService.startWorkoutSession(user.getId(), splitId, workoutId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sessions/{sessionId}/sets")
    public ResponseEntity<LogSetResponse> logSet(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId,
            @Valid @RequestBody LogSetRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var response = workoutSessionService.logSet(user.getId(), sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/sessions/{sessionId}/finish")
    public ResponseEntity<FinishSessionResponse> finishSession(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId,
            @RequestBody FinishSessionRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var response = workoutSessionService.finishSession(user.getId(), sessionId, request);
        return ResponseEntity.ok(response);
    }


 }
