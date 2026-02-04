package com.Sorensen.FitMark.dto.workout;

import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.Workout;

import java.util.List;
import java.util.UUID;

public record WorkoutResponse (UUID id, UUID userId, UUID splitID, String userName, Integer pos ,String title, List<Exercise> exercises, String notes){


}
