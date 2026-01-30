package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.auth.RegisterUserResponse;
import com.Sorensen.FitMark.dto.split.SplitCreateRequest;
import com.Sorensen.FitMark.dto.split.SplitCreateResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.SplitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/splits")
public class SplitController {

    private final SplitService splitService;

    public SplitController(SplitService splitService) {
        this.splitService = splitService;
    }

    @PostMapping
    public ResponseEntity<SplitCreateResponse> splitCreate(@AuthenticationPrincipal User user, @Valid @RequestBody SplitCreateRequest splitCreateRequest) {
        var userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var split = splitService.createSplit(splitCreateRequest, userId);


        return ResponseEntity.status(HttpStatus.CREATED).body(new SplitCreateResponse(
                split.splitId(),
                split.userId(),
                split.title()
        ));


    }


}
