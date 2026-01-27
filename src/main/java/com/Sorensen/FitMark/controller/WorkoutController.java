package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.workout.CreateWorkoutRequest;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/create")
    public ResponseEntity<WorkoutResponse> createWorkout(@AuthenticationPrincipal User user, @Valid @RequestBody CreateWorkoutRequest req) {

        var id = user.getId();
        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var workout = workoutService.createWorkout(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkoutResponse(workout.id(), workout.userId(),workout.userName(), workout.title(), workout.exercises(), workout.notes()));

    }


    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(@AuthenticationPrincipal User user, @PathVariable UUID workoutId) {




        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = workoutService.deleteWorkout(user, workoutId);


        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).build();

        }
        else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
}