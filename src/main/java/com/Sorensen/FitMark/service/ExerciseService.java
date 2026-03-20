package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.dto.ExerciseLog.ExerciseLogDetailsResponse;
import com.Sorensen.FitMark.dto.exercise.AddExerciseRequest;
import com.Sorensen.FitMark.dto.exercise.AddExerciseResponse;
import com.Sorensen.FitMark.dto.exercise.GetExerciseDetailsResponse;
import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Service
public class ExerciseService {

  private final EntityFinder entityFinder;
    private final ExerciseRepository exerciseRepository;
    private final SetLogRepository setLogRepository;


    public ExerciseService(UserRepository userRepository, WorkoutRepository workoutRepository, SplitRepository splitRepository, EntityFinder entityFinder, ExerciseRepository exerciseRepository, SetLogRepository setLogRepository, ExerciseLogService exerciseLogService) {
        this.entityFinder = entityFinder;

        this.exerciseRepository = exerciseRepository;
        this.setLogRepository = setLogRepository;
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

        var exPos = exerciseRepository.findTopByWorkoutIdOrderByPositionDesc(workoutId).map(e -> e.getPosition() + 1).orElse(0);


        Exercise exercise = Exercise.builder()
                .name(request.name())
                .sets(request.sets())
                .workout(workout)
                .position(exPos)
                .build();

        exerciseRepository.save(exercise);
        return new AddExerciseResponse(exercise.getName(),exercise.getWorkout().getTitle(), user.getUsername(), exercise.getPosition());

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

        // PATCH seguro: altera só o que o usuário pode editar
        exercise.setName(request.name());
        exercise.setSets(request.sets());

        exercise = exerciseRepository.save(exercise);

        return new AddExerciseResponse(
                exercise.getName(),
                exercise.getWorkout().getTitle(),
                user.getUsername(),
                exercise.getPosition()
        );
    }

    public boolean deleteExercise(UUID userid, @NotNull UUID exerciseId) {

        var exercise = entityFinder.exercise(exerciseId);
        if (exercise.getWorkout().getUser().getId().equals(userid)){
            exerciseRepository.delete(exercise);
            return true;
        }else return false;
    }

    public Optional<GetExerciseDetailsResponse> getExercise(UUID userid, UUID exerciseId) {
    var exercise = entityFinder.exercise(exerciseId);
    if (exercise.getWorkout().getUser().getId().equals(userid)){
        return Optional.of(new GetExerciseDetailsResponse(exercise.getName(),
                exercise.getWorkout().getTitle(),
                exercise.getSets(),
                exercise.getWeight(),
                exercise.getLastTopSetReps(),
                exercise.getPosition()));
    }else return Optional.empty();
    }

    public Optional<List<ExerciseLogDetailsResponse>> getExerciseLogs(UUID userId, UUID exerciseId) {

        var exercise = entityFinder.exercise(exerciseId);
        if (!exercise.getWorkout().getUser().getId().equals(userId)) {
            return Optional.empty();
        }

        var logs = setLogRepository.findByExerciseIdOrderByCreatedAtDesc(exerciseId);

        var response = logs.stream()
                .map(log -> new ExerciseLogDetailsResponse(
                        exercise.getId(),
                        exercise.getName(),
                        log.getSetNumber(),
                        log.getReps(),
                        log.getWeight(),
                        log.getRestSeconds(),
                        exercise.getLastTopSetReps(),
                        Date.from(log.getCreatedAt().toInstant())
                ))
                .toList();

        return Optional.of(response);
    }
}