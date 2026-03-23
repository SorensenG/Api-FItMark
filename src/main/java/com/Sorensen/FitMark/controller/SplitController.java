package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.auth.RegisterUserResponse;
import com.Sorensen.FitMark.dto.split.SplitCreateRequest;
import com.Sorensen.FitMark.dto.split.SplitCreateResponse;
import com.Sorensen.FitMark.dto.split.SplitDetailsResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.SplitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/splits")
public class SplitController {

    private final SplitService splitService;

    public SplitController(SplitService splitService) {
        this.splitService = splitService;
    }

    @GetMapping()
    public ResponseEntity<List<SplitDetailsResponse>> getSplits(@AuthenticationPrincipal User user) {
        var userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var splits = splitService.getSplitsForUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(splits);
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

    @DeleteMapping("{splitId}")
    public ResponseEntity<Void> deleteSplit(@AuthenticationPrincipal User user, @PathVariable UUID splitId) {
        var userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var deleted = splitService.deleteSplit(userId, splitId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
