package com.Sorensen.FitMark.Util;

import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.entity.Workout;
import com.Sorensen.FitMark.entity.WorkoutSession;
import com.Sorensen.FitMark.repository.ExerciseRepository;
import com.Sorensen.FitMark.repository.SplitRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import com.Sorensen.FitMark.repository.WorkoutRepository;
import com.Sorensen.FitMark.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EntityFinder {
    private final UserRepository userRepository;
    private final SplitRepository splitRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;

    public EntityFinder(UserRepository userRepository,
                        SplitRepository splitRepository,
                        WorkoutRepository workoutRepository,
                        ExerciseRepository exerciseRepository,
                        WorkoutSessionRepository workoutSessionRepository) {
        this.userRepository = userRepository;
        this.splitRepository = splitRepository;
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutSessionRepository = workoutSessionRepository;
    }

    public User user(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Split split(UUID splitId) {
        return splitRepository.findById(splitId)
                .orElseThrow(() -> new IllegalArgumentException("Split not found"));
    }

    public Workout workout(UUID workoutId) {
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found"));
    }

    public Exercise exercise(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public WorkoutSession workoutSession(UUID sessionId) {
        return workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Workout session not found"));
    }
}
