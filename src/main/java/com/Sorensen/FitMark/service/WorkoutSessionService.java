package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.Util.EntityFinder;
import com.Sorensen.FitMark.dto.exercise.ExerciseSessionResponse;
import com.Sorensen.FitMark.dto.workout.*;
import com.Sorensen.FitMark.entity.Exercise;
import com.Sorensen.FitMark.entity.SetLog;
import com.Sorensen.FitMark.entity.SetType;
import com.Sorensen.FitMark.entity.WorkoutSession;
import com.Sorensen.FitMark.repository.ExerciseRepository;
import com.Sorensen.FitMark.repository.SetLogRepository;
import com.Sorensen.FitMark.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkoutSessionService {
    private final EntityFinder finder;
    private final WorkoutSessionRepository sessionRepository;
    private final SetLogRepository setLogRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutSessionService(EntityFinder finder,
                                 WorkoutSessionRepository repository,
                                 SetLogRepository setLogRepository,
                                 ExerciseRepository exerciseRepository)
    {
        this.finder = finder;
        this.sessionRepository = repository;
        this.setLogRepository = setLogRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public StartWorkOutSessionResponse startWorkoutSession(UUID userId, UUID splitId, UUID workoutID) {

        var workout = finder.workout(workoutID);
        var user = finder.user(userId);

        if (workout.getUser() == null || !workout.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Workout does not belong to user");
        }

        if (workout.getSplit() == null || !workout.getSplit().getId().equals(splitId)) {
            throw new IllegalArgumentException("Workout does not belong to split");
        }

        //checar se o último treino foi finalizado
        boolean onGoingSession = sessionRepository.hasActiveSession(userId).orElse(false);

        if (onGoingSession) {
            throw new IllegalStateException("Cannot start a new session while another is active");
        }

        WorkoutSession workoutSession = WorkoutSession.builder()
                .user(user)
                .workout(workout)
                .workoutDate(OffsetDateTime.now())
                .completed(false)
                .build();

        workoutSession = sessionRepository.save(workoutSession);

        List<ExerciseSessionResponse> exercises = workout.getExercises()
                .stream()
                .map(e -> new ExerciseSessionResponse(
                        e.getId(),
                        e.getName(),
                        e.getSets(),
                        e.getLastTopSetReps(),
                        e.getWeight(),
                        e.getPosition()))
                .toList();

        return new StartWorkOutSessionResponse(
                workoutSession.getId(),
                workout.getId(),
                workout.getTitle(),
                workoutSession.getWorkoutDate(),
                workoutSession.getCompleted(),
                exercises
        );
    }

    public LogSetResponse logSet(UUID userId, UUID sessionId, LogSetRequest request) {
        WorkoutSession session = finder.workoutSession(sessionId);

        if (!session.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Session does not belong to user");
        }

        if (session.getCompleted()) {
            throw new IllegalStateException("Cannot log sets on a completed session");
        }

        Exercise exercise = finder.exercise(request.exerciseId());

        if (!exercise.getWorkout().getId().equals(session.getWorkout().getId())) {
            throw new IllegalArgumentException("Exercise does not belong to this workout");
        }

        SetLog setLog = SetLog.builder()
                .workoutSession(session)
                .exercise(exercise)
                .setNumber(request.setNumber())
                .reps(request.reps())
                .setType(request.setType())
                .weight(request.weight())
                .restSeconds(request.restSeconds() != null ? request.restSeconds() : 0)
                .build();

        setLog = setLogRepository.save(setLog);

        return new LogSetResponse(
                setLog.getId(),
                session.getId(),
                exercise.getId(),
                setLog.getSetNumber(),
                setLog.getReps(),
                setLog.getSetType(),
                setLog.getWeight(),
                setLog.getRestSeconds(),
                setLog.getCreatedAt()
        );
    }

    @Transactional
    public FinishSessionResponse finishSession(UUID userId, UUID sessionId, FinishSessionRequest request) {
        WorkoutSession session = finder.workoutSession(sessionId);

        if (!session.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Session does not belong to user");
        }

        if (session.getCompleted()) {
            throw new IllegalStateException("Session is already completed");
        }

        session.setCompleted(true);
        session.setDurationMinutes(request.durationMinutes());
        session.setNotes(request.notes());

        // Update exercise cache: for each exercise, find the heaviest WORK set and persist it
        Map<UUID, List<SetLog>> setsByExercise = session.getSets().stream()
                .filter(s -> s.getSetType() == SetType.WORK)
                .collect(Collectors.groupingBy(s -> s.getExercise().getId()));

        setsByExercise.forEach((exerciseId, sets) -> {
            SetLog topSet = sets.stream()
                    .max(Comparator.comparing(s -> s.getWeight() != null ? s.getWeight() : BigDecimal.ZERO))
                    .orElse(null);

            if (topSet != null) {
                Exercise exercise = topSet.getExercise();
                exercise.setWeight(topSet.getWeight());
                exercise.setLastTopSetReps(topSet.getReps());
                exerciseRepository.save(exercise);
            }
        });

        session = sessionRepository.save(session);

        // Resumo por exercício
        Map<String, List<SetLog>> setsByExerciseName = session.getSets().stream()
                .collect(Collectors.groupingBy(s -> s.getExercise().getName()));

        List<ExerciseSummary> exerciseSummaries = setsByExerciseName.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<SetLog> sets = entry.getValue();
                    int totalSets = sets.size();
                    int totalReps = sets.stream().mapToInt(SetLog::getReps).sum();
                    BigDecimal totalVolume = sets.stream()
                            .filter(s -> s.getWeight() != null)
                            .map(s -> s.getWeight().multiply(BigDecimal.valueOf(s.getReps())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    SetLog topSet = sets.stream()
                            .filter(s -> s.getWeight() != null)
                            .max(Comparator.comparing(s -> s.getWeight().multiply(BigDecimal.valueOf(s.getReps()))))
                            .orElse(null);

                    BigDecimal topSetWeight = topSet != null ? topSet.getWeight() : null;
                    Integer topSetReps = topSet != null ? topSet.getReps() : null;
                    BigDecimal topSetVolume = topSet != null
                            ? topSet.getWeight().multiply(BigDecimal.valueOf(topSet.getReps()))
                            : null;

                    return new ExerciseSummary(name, totalSets, totalReps, totalVolume, topSetWeight, topSetReps, topSetVolume);
                })
                .toList();

        BigDecimal totalVolumeKg = exerciseSummaries.stream()
                .map(ExerciseSummary::totalVolume)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new FinishSessionResponse(
                session.getId(),
                session.getWorkout().getId(),
                session.getWorkout().getTitle(),
                session.getWorkoutDate(),
                session.getCompleted(),
                session.getDurationMinutes(),
                session.getNotes(),
                exerciseSummaries,
                totalVolumeKg
        );
    }

    public List<ListAllSessionsResponse> listAllSessions(UUID id) {
        finder.user(id);

        return sessionRepository.findByUserIdOrderByWorkoutDateDesc(id)
                .orElse(Collections.emptyList())
                .stream()
                .filter(WorkoutSession::getCompleted)  // só sessões concluídas
                .map(s -> new ListAllSessionsResponse(
                        s.getId(),
                        s.getWorkout().getTitle(),
                        s.getNotes(),
                        s.getCompleted(),
                        s.getDurationMinutes(),
                        s.getWorkoutDate()
                ))
                .toList();
    }

    public AbandonSessionResponse abandonSession(UUID userId, UUID sessionId) {
        WorkoutSession session = finder.workoutSession(sessionId);

        if (!session.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Session does not belong to user");
        }

        if (session.getCompleted()) {
            throw new IllegalStateException("Cannot abandon a completed session");
        }

        if (session.getAbandoned()) {
            throw new IllegalStateException("Session is already abandoned");
        }

        session.setAbandoned(true);
        sessionRepository.save(session);

        return new AbandonSessionResponse(session.getId(), session.getAbandoned());
    }

    public SessionDetailsResponse getSessionDetails(UUID userId, UUID sessionId) {
        WorkoutSession session = finder.workoutSession(sessionId);

        if (!session.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Session does not belong to user");
        }

        Map<Exercise, List<SetLog>> setsByExercise = session.getSets().stream()
                .collect(Collectors.groupingBy(SetLog::getExercise));

        List<ExerciseWithSetsResponse> exercises = setsByExercise.entrySet().stream()
                .map(entry -> {
                    Exercise ex = entry.getKey();
                    List<SetLogDetails> sets = entry.getValue().stream()
                            .sorted(Comparator.comparing(SetLog::getSetNumber))
                            .map(s -> new SetLogDetails(
                                    s.getSetNumber(),
                                    s.getReps(),
                                    s.getWeight(),
                                    s.getSetType(),
                                    s.getRestSeconds()
                            ))
                            .toList();
                    return new ExerciseWithSetsResponse(ex.getId(), ex.getName(), sets);
                })
                .toList();

        return new SessionDetailsResponse(
                session.getId(),
                session.getWorkout().getId(),
                session.getWorkout().getTitle(),
                session.getWorkoutDate(),
                session.getCompleted(),
                session.getDurationMinutes(),
                session.getNotes(),
                exercises
        );
    }
}

