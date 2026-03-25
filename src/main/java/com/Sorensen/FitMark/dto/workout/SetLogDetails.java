package com.Sorensen.FitMark.dto.workout;

import com.Sorensen.FitMark.entity.SetType;

import java.math.BigDecimal;

public record SetLogDetails(
        Integer setNumber,
        Integer reps,
        BigDecimal weight,
        SetType setType,
        Integer restSeconds
) {}
