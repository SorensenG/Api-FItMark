package com.Sorensen.FitMark.dto.exercise;

import java.math.BigDecimal;

public record AddExerciseRequest(String name, Integer sets, BigDecimal weight, Integer lastTopSetReps) {
}
