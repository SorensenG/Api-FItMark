package com.Sorensen.FitMark.controller;


import com.Sorensen.FitMark.dto.exercise.AddExerciseRequest;
import com.Sorensen.FitMark.dto.exercise.AddExerciseResponse;
import com.Sorensen.FitMark.dto.exercise.GetExerciseDetailsResponse;
import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.ExerciseRepository;
import com.Sorensen.FitMark.service.ExerciseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/splits/{splitId}/workouts/{workoutId}/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseRepository exerciseRepository, ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }


    @PostMapping
    public ResponseEntity<AddExerciseResponse> addExercise(@AuthenticationPrincipal @NotNull User user,
                                                           @PathVariable @NotNull UUID splitId,
                                                           @PathVariable @NotNull UUID workoutId,
                                                           @Valid @RequestBody AddExerciseRequest request) {
        var userid = user.getId();
        var exercise = exerciseService.addExercise(userid, splitId, workoutId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddExerciseResponse(
                        exercise.exerciseName(),
                        exercise.workoutName(),
                        exercise.Username(),
                exercise.exercisePosition()
                )
        );


    }


    @PutMapping("/{exerciseId}")
    public ResponseEntity<AddExerciseResponse> updateExercise(@AuthenticationPrincipal @NotNull User user,
                                                              @PathVariable @NotNull UUID splitId,
                                                              @PathVariable @NotNull UUID workoutId,
                                                              @PathVariable @NotNull UUID exerciseId,
                                                              @Valid @RequestBody AddExerciseRequest request) {
        var userid = user.getId();
        var exercise = exerciseService.updateExercise(userid, splitId, workoutId, exerciseId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new AddExerciseResponse(
                exercise.exerciseName(),
                exercise.workoutName(),
                exercise.Username(),
                exercise.exercisePosition()));


    }

@DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(@AuthenticationPrincipal @NotNull User user,
                                               @PathVariable @NotNull UUID exerciseId) {
        var userid = user.getId();
        boolean deleted = exerciseService.deleteExercise(userid,exerciseId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

@GetMapping("/{exerciseId}")
    public ResponseEntity<GetExerciseDetailsResponse> getExercise(@AuthenticationPrincipal User user,
                                                                  @PathVariable UUID exerciseId){

        var userid = user.getId();

        Optional<GetExerciseDetailsResponse> exercise = exerciseService.getExercise(userid, exerciseId);
        if (exercise.isPresent()){

            return ResponseEntity.status(HttpStatus.OK).body((new GetExerciseDetailsResponse(exercise.get().name(),
                    exercise.get().workoutName(),
                    exercise.get().sets(),
                    exercise.get().maxWeight(),
                    exercise.get().lastTopReps(),
                    exercise.get().positionInWorkout())));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

}

}






