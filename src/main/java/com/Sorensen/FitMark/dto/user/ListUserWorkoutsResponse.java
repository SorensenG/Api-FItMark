package com.Sorensen.FitMark.dto.user;

import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.Workout;

import java.util.List;

public record ListUserWorkoutsResponse(List<WorkoutResponse> workouts, Integer totalWorkouts) {
}
