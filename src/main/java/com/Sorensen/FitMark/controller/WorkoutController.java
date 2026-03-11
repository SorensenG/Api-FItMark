package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.workout.CreateWorkoutRequest;
import com.Sorensen.FitMark.dto.workout.StartWorkOutSessionResponse;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/splits/{splitId}/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@AuthenticationPrincipal User user, @PathVariable UUID splitId, @Valid @RequestBody CreateWorkoutRequest req) {

        var id = user.getId();
        if (id == null || splitId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var workout = workoutService.createWorkout(id, req, splitId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new WorkoutResponse(workout.id(), workout.userId(), workout.splitID(), workout.userName(), workout.pos(), workout.title(), workout.exercises(), workout.notes()));

    }


    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(@AuthenticationPrincipal User user, @PathVariable UUID workoutId) {


        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = workoutService.deleteWorkout(user, workoutId);


        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).build();

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @GetMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponse> getWorkout(@AuthenticationPrincipal User user, @PathVariable UUID workoutId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var id = user.getId();
        var workoutOpt = workoutService.getWorkout(id, workoutId);

        return ResponseEntity.status(HttpStatus.OK).body(new WorkoutResponse(workoutOpt.id(), workoutOpt.userId(), workoutOpt.splitID(), workoutOpt.userName(), workoutOpt.pos(), workoutOpt.title(), workoutOpt.exercises(), workoutOpt.notes()));


    }

    @PutMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponse> updateWorkout(@AuthenticationPrincipal User user, @PathVariable UUID workoutId, @Valid @RequestBody CreateWorkoutRequest req) {



        //A ser implementado
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        var id = user.getId();
//        var workoutOpt = workoutService.updateWorkout(id, workoutId, req);
//
//        return ResponseEntity.status(HttpStatus.OK).body(new WorkoutResponse(workoutOpt.id(), workoutOpt.userId(), workoutOpt.splitID(), workoutOpt.userName(), workoutOpt.pos(), workoutOpt.title(), workoutOpt.exercises(), workoutOpt.notes()));

        return null;

    }


}

