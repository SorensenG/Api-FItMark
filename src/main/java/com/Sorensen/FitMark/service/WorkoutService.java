package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.dto.user.ListUserWorkoutsResponse;
import com.Sorensen.FitMark.dto.workout.CreateWorkoutRequest;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.repository.SplitRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import com.Sorensen.FitMark.repository.WorkoutRepository;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkoutService {
    private final WorkoutRepository repository;
    private final UserRepository userRepository;
    private final SplitRepository splitRepository;


    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository, SplitRepository splitRepository) {
        this.repository = workoutRepository;
        this.userRepository = userRepository;
        this.splitRepository = splitRepository;
    }


    public WorkoutResponse createWorkout(UUID userId, CreateWorkoutRequest request, UUID splitId) {

        Optional<User> user = userRepository.findById(userId);
        Optional<Split> split = splitRepository.findById(splitId);

        int workoutPos = repository.findTopByUserIdAndSplitIdOrderByPositionDesc(userId, splitId)
                .map(w -> w.getPosition() + 1).orElse(0);

        if (user.isPresent() && split.isPresent()) {
            User present = user.get();
            Split presentSplit = split.get();


            Workout workout = Workout.builder()
                    .user(present)
                    .split(presentSplit)
                    .position(workoutPos)
                    .title(request.title())
                    .notes(request.notes())
                    .build();

            workout = repository.save(workout);


            return new WorkoutResponse(
                    workout.getId(),
                    workout.getUser().getId(),
                    workout.getSplit().getId(),
                   workout.getUser().getUsername(),
                    workout.getPosition(),
                    workout.getTitle(),
                    workout.getExercises(),
                    workout.getNotes()
            );
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public boolean deleteWorkout(User user, UUID workoutId) {
        if (!repository.existsByIdAndUserId(workoutId, user.getId())) {

            return false;
        } else {
            repository.deleteByIdAndUserId(workoutId, user.getId());
            return true;
        }
    }

    public ListUserWorkoutsResponse listWorkout(UUID userId) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<Workout> workouts = repository.findAllByUserId(userId);
            return new ListUserWorkoutsResponse(
                    workouts.stream().map(workout -> new WorkoutResponse(
                           workout.getId(),
                            workout.getUser().getId(),
                            workout.getSplit() != null ? workout.getSplit().getId() : null,
                            workout.getUser().getUsername(),
                            workout.getPosition(),
                            workout.getTitle(),
                            workout.getExercises(),
                            workout.getNotes()
                    )).toList(),
                    workouts.size()
            );
        } else {
            throw new IllegalArgumentException("User not found");

        }


    }

    public WorkoutResponse getWorkout(UUID userId, UUID workoutId) {

        Optional<Workout> workoutOpt = repository.findByIdAndUserId(workoutId, userId);

        if (workoutOpt.isPresent()) {
            Workout workout = workoutOpt.get();
            return new WorkoutResponse(
                    workout.getId(),
                    workout.getUser().getId(),
                    workout.getSplit() != null ? workout.getSplit().getId() : null,
                    workout.getUser().getUsername(),
                    workout.getPosition(),
                    workout.getTitle(),
                    workout.getExercises(),
                    workout.getNotes()
            );
        } else {
            throw new IllegalArgumentException("Workout not found");
        }


    }

    public WorkoutResponse updateWorkout(UUID id, UUID workoutId, @Valid CreateWorkoutRequest req) {

    //A ser implementado
        return null;
    }

}





