package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.repository.SetLogRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseLogService {
private final SetLogRepository setLogRepository;

    public ExerciseLogService(SetLogRepository setLogRepository) {
        this.setLogRepository = setLogRepository;
    }



}
