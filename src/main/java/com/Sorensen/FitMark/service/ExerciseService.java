package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.dto.exercise.AddExerciseRequest;
import com.Sorensen.FitMark.dto.exercise.AddExerciseResponse;
import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.repository.ExerciseRepository;
import com.Sorensen.FitMark.repository.SplitRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import com.Sorensen.FitMark.repository.WorkoutRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;



@Service
public class ExerciseService {

  private final EntityFinder entityFinder;
    private final ExerciseRepository exerciseRepository;


    public ExerciseService(UserRepository userRepository, WorkoutRepository workoutRepository, SplitRepository splitRepository, EntityFinder entityFinder, ExerciseRepository exerciseRepository) {
        this.entityFinder = entityFinder;

        this.exerciseRepository = exerciseRepository;
    }


    public AddExerciseResponse addExercise(UUID userid, UUID splitId, UUID workoutId, @Valid AddExerciseRequest request) {

        var user = entityFinder.user(userid);
        var split = entityFinder.split(splitId);
        var workout = entityFinder.workout(workoutId);

        if (split.getUser() == null || !split.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Split does not belong to user");
        }

        if (workout.getSplit() == null || !workout.getSplit().getId().equals(split.getId())) {
            throw new IllegalArgumentException("Workout does not belong to split");
        }

        Exercise exercise = Exercise.builder()
                .name(request.name())
                .sets(request.sets())
                .workout(workout)
                .build();

        exerciseRepository.save(exercise);
        return new AddExerciseResponse(exercise.getName(),exercise.getWorkout().getTitle(), user.getUsername());

    }

    public AddExerciseResponse updateExercise(UUID userid, @NotNull UUID splitId, @NotNull UUID workoutId, @NotNull UUID exerciseId, @Valid AddExerciseRequest request) {

        var exercise = entityFinder.exercise(exerciseId);
        var user = entityFinder.user(userid);
        var split = entityFinder.split(splitId);
        var workout = entityFinder.workout(workoutId);

        if (split.getUser() == null || !split.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Split does not belong to user");
        }

        if (workout.getSplit() == null || !workout.getSplit().getId().equals(split.getId())) {
            throw new IllegalArgumentException("Workout does not belong to split");
        }

        if (exercise.getWorkout()  == null || !exercise.getWorkout().getId().equals(workoutId)) {
            throw  new IllegalArgumentException("Exercise does not belong to workout");
        }

        Exercise updatedExercise = Exercise.builder()
                .id(exercise.getId())
                .name(request.name())
                .sets(request.sets())
                .workout(workout)
                .build();

        exerciseRepository.save(updatedExercise);

        return new AddExerciseResponse(exercise.getName(),exercise.getWorkout().getTitle(), user.getUsername());

    }

    public boolean deleteExercise(UUID userid, @NotNull UUID exerciseId) {

        var exercise = entityFinder.exercise(exerciseId);
        if (exercise.getWorkout().getUser().getId().equals(userid)){
            exerciseRepository.delete(exercise);
            return true;
        }else return false;
    }
}
