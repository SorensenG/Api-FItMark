package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.dto.split.SplitCreateRequest;
import com.Sorensen.FitMark.dto.split.SplitCreateResponse;
import com.Sorensen.FitMark.entity.Split;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.SplitRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SplitService {

    private final SplitRepository splitRepository;
    private final UserRepository userRepository;

    public SplitService(SplitRepository splitRepository, UserRepository userRepository) {
        this.splitRepository = splitRepository;
        this.userRepository = userRepository;
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


}
