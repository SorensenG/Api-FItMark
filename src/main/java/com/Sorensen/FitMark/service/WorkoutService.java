package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.dto.user.ListUserWorkoutsResponse;
import com.Sorensen.FitMark.dto.workout.CreateWorkoutRequest;
import com.Sorensen.FitMark.dto.workout.WorkoutExerciseResponse;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.repository.SplitRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import com.Sorensen.FitMark.repository.WorkoutRepository;
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


    @Transactional
    public WorkoutResponse createWorkout(UUID userId, CreateWorkoutRequest request, UUID splitId) {

        Optional<User> user = userRepository.findById(userId);
        Optional<Split> split = splitRepository.findById(splitId);

        int workoutPos = repository.findTopByUserIdAndSplitIdOrderByPositionDesc(userId, splitId)
                .map(w -> w.getPosition() + 1).orElse(0);

        if (user.isPresent() && split.isPresent()) {
            User present = user.get();
            Split presentSplit = split.get();

            if (!presentSplit.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("Split does not belong to user");
            }

            Workout workout = Workout.builder()
                    .user(present)
                    .split(presentSplit)
                    .position(workoutPos)
                    .title(request.title())
                    .notes(request.notes())
                    .build();

            workout = repository.save(workout);


            return toWorkoutResponse(workout);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Transactional
    public boolean deleteWorkout(User user, UUID splitId, UUID workoutId) {
        if (!repository.existsByIdAndUserIdAndSplitId(workoutId, user.getId(), splitId)) {

            return false;
        } else {
            repository.deleteByIdAndUserIdAndSplitId(workoutId, user.getId(), splitId);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public ListUserWorkoutsResponse listWorkout(UUID userId) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<Workout> workouts = repository.findAllByUserId(userId);
            return new ListUserWorkoutsResponse(
                    workouts.stream().map(this::toWorkoutResponse).toList(),
                    workouts.size()
            );
        } else {
            throw new IllegalArgumentException("User not found");

        }


    }

    @Transactional(readOnly = true)
    public WorkoutResponse getWorkout(UUID userId, UUID splitId, UUID workoutId) {

        Optional<Workout> workoutOpt = repository.findByIdAndUserIdAndSplitId(workoutId, userId, splitId);

        if (workoutOpt.isPresent()) {
            return toWorkoutResponse(workoutOpt.get());
        } else {
            throw new IllegalArgumentException("Workout not found");
        }


    }

    @Transactional
    public WorkoutResponse updateWorkout(UUID userId, UUID splitId, UUID workoutId, @Valid CreateWorkoutRequest req) {
        Workout workout = repository.findByIdAndUserIdAndSplitId(workoutId, userId, splitId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found or does not belong to user"));

        workout.setTitle(req.title());
        workout.setNotes(req.notes());
        workout = repository.save(workout);

        return toWorkoutResponse(workout);
    }

    private WorkoutResponse toWorkoutResponse(Workout workout) {
        return new WorkoutResponse(
                workout.getId(),
                workout.getUser().getId(),
                workout.getSplit() != null ? workout.getSplit().getId() : null,
                workout.getUser().getUsername(),
                workout.getPosition(),
                workout.getTitle(),
                workout.getExercises().stream()
                        .map(this::toWorkoutExerciseResponse)
                        .toList(),
                workout.getNotes()
        );
    }

    private WorkoutExerciseResponse toWorkoutExerciseResponse(Exercise exercise) {
        return new WorkoutExerciseResponse(
                exercise.getId(),
                exercise.getName(),
                exercise.getSets(),
                exercise.getLastTopSetReps(),
                exercise.getWeight(),
                exercise.getCreatedAt(),
                exercise.getPosition()
        );
    }
}



