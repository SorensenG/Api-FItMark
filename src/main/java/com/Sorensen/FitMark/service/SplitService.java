package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.dto.exercise.ExerciseSessionResponse;
import com.Sorensen.FitMark.dto.split.SplitCreateRequest;
import com.Sorensen.FitMark.dto.split.SplitCreateResponse;
import com.Sorensen.FitMark.dto.split.SplitDetailsResponse;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.SplitRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SplitService {

    private final SplitRepository splitRepository;
    private final UserRepository userRepository;
    private final EntityFinder entityFinder;

    public SplitService(SplitRepository splitRepository, UserRepository userRepository, EntityFinder entityFinder) {
        this.splitRepository = splitRepository;
        this.userRepository = userRepository;
        this.entityFinder = entityFinder;
    }

    public SplitCreateResponse createSplit(SplitCreateRequest request, UUID userid) {

        Optional<User> user = userRepository.findById(userid);

        if (user.isPresent()) {

            var present = user.get();

            var split = Split.builder()
                    .user(present)
                    .name(request.title())
                    .build();


            splitRepository.save(split);

            return new SplitCreateResponse(split.getId(), present.getId(), split.getName());


        } else {
            throw new IllegalArgumentException("User not found");
        }


    }


    public List<SplitDetailsResponse> getSplitsForUser(UUID userId) {
        return splitRepository.findByUserId(userId).stream()
                .map(this::toSplitDetailsResponse)
                .toList();
    }

    private SplitDetailsResponse toSplitDetailsResponse(Split split) {
        var workouts = split.getWorkouts().stream()
                .map(workout -> new SplitDetailsResponse.WorkoutSummary(
                        workout.getId(),
                        workout.getTitle(),
                        workout.getPosition(),
                        workout.getExercises().stream()
                                .map(exercise -> new ExerciseSessionResponse(
                                        exercise.getId(),
                                        exercise.getName(),
                                        exercise.getSets(),
                                        exercise.getLastTopSetReps(),
                                        exercise.getWeight(),
                                        exercise.getPosition()
                                ))
                                .toList()
                ))
                .toList();
        return new SplitDetailsResponse(split.getId(), split.getName(), split.getCreatedAt(), workouts);
    }

    public boolean deleteSplit(UUID userId, UUID splitId) {

        var split = entityFinder.split(splitId);

        if (split.getUser().getId().equals(userId)){
            splitRepository.deleteById(splitId);
            return true;

    }else {
            throw new IllegalArgumentException("Split does not belong to user");
        }
    }

    public SplitDetailsResponse getSplitDetails(UUID userId, UUID splitId) {

        var split = entityFinder.split(splitId);

        if (split.getUser().getId().equals(userId)){
            return toSplitDetailsResponse(split);
        }else {
            throw new IllegalArgumentException("Split does not belong to user");
        }



    }
}
