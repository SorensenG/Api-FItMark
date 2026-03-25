package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.user.ListUserWorkoutsResponse;
import com.Sorensen.FitMark.dto.workout.AbandonSessionResponse;
import com.Sorensen.FitMark.dto.workout.ListAllSessionsResponse;
import com.Sorensen.FitMark.dto.workout.SessionDetailsResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.UserService;
import com.Sorensen.FitMark.service.WorkoutService;
import com.Sorensen.FitMark.service.WorkoutSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final WorkoutService workoutService;
    private final WorkoutSessionService workoutSessionService;

    public UserController(UserService userService, WorkoutService workoutService, WorkoutSessionService workoutSessionService) {
        this.workoutService = workoutService;
        this.workoutSessionService = workoutSessionService;
    }


    @GetMapping("/workouts")
    public ResponseEntity<ListUserWorkoutsResponse> listUserWorkouts(@AuthenticationPrincipal User user) {

        if (user == null || user.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var res = workoutService.listWorkout(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ListUserWorkoutsResponse(res.workouts(), res.totalWorkouts()));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ListAllSessionsResponse>> listAllSesions(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ListAllSessionsResponse> response = workoutSessionService.listAllSessions(user.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/sessions/{sessionId}/abandon")
    public ResponseEntity<AbandonSessionResponse> abandonSession(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AbandonSessionResponse response = workoutSessionService.abandonSession(user.getId(), sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDetailsResponse> getSessionDetails(
            @AuthenticationPrincipal User user,
            @PathVariable UUID sessionId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SessionDetailsResponse response = workoutSessionService.getSessionDetails(user.getId(), sessionId);
        return ResponseEntity.ok(response);
    }
}

