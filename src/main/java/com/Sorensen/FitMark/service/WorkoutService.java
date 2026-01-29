package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.dto.workout.CreateWorkoutRequest;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.UserRepository;
import com.Sorensen.FitMark.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class WorkoutService {
    private final WorkoutRepository repository;
    private final UserRepository userRepository;


    public WorkoutService(WorkoutRepository workoutRepository, UserRepository userRepository) {
        this.repository = workoutRepository;
        this.userRepository = userRepository;
    }


    public WorkoutResponse createWorkout(UUID userId, CreateWorkoutRequest request) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            User present = user.get();


            var split = Split.builder()
                    .name(request.title())
                    .user(present)
                    .build();

//            Workout workout = Workout.builder()
//                    .user(present)
//                    .title(request.title())
//                    .notes(request.notes())
//                    .createdAt(request.date())
//                    .build();

            split = repository.save(split);


            return new WorkoutResponse(
                    workout.getId(),
                    workout.getUser().getId(),
                    workout.getUser().getUsername(),
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
}



