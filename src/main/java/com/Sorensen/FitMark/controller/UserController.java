package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.user.ListUserWorkoutsResponse;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.service.UserService;
import com.Sorensen.FitMark.service.WorkoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final WorkoutService workoutService;

    public UserController(UserService userService, WorkoutService workoutService) {
        this.userService = userService;
        this.workoutService = workoutService;
    }


    @GetMapping("/workouts")
    public ResponseEntity<ListUserWorkoutsResponse> listUserWorkouts(@AuthenticationPrincipal User user) {

        if (user == null || user.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var res = workoutService.listWorkout(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ListUserWorkoutsResponse(res.workouts(), res.totalWorkouts()));

    }
}

