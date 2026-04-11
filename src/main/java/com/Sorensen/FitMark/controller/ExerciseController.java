package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.ExerciseLog.ExerciseLogDetailsResponse;
import com.Sorensen.FitMark.dto.error.ApiError;
import com.Sorensen.FitMark.dto.exercise.AddExerciseRequest;
import com.Sorensen.FitMark.dto.exercise.AddExerciseResponse;
import com.Sorensen.FitMark.dto.exercise.GetExerciseDetailsResponse;
import com.Sorensen.FitMark.dto.exercise.ReorderExercisesRequest;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.ExerciseRepository;
import com.Sorensen.FitMark.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Exercises", description = "Gerenciamento de exercícios dentro de um treino")
@RestController
@RequestMapping("/splits/{splitId}/workouts/{workoutId}/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseRepository exerciseRepository, ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Operation(summary = "Adicionar exercício a um treino")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exercício adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Split não pertence ao usuário, ou workout não pertence ao split",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Split, workout ou usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos (name ou sets ausentes)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<AddExerciseResponse> addExercise(
            @AuthenticationPrincipal @NotNull User user,
            @Parameter(description = "ID do split") @PathVariable @NotNull UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable @NotNull UUID workoutId,
            @Valid @RequestBody AddExerciseRequest request) {

        var exercise = exerciseService.addExercise(user.getId(), splitId, workoutId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddExerciseResponse(
                exercise.exerciseName(),
                exercise.workoutName(),
                exercise.Username(),
                exercise.exercisePosition()
        ));
    }

    @Operation(summary = "Atualizar exercício", description = "Atualiza nome e número de séries do exercício.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercício atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Split não pertence ao usuário, workout não pertence ao split, ou exercício não pertence ao workout",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Split, workout ou exercício não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos (name ou sets ausentes)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{exerciseId}")
    public ResponseEntity<AddExerciseResponse> updateExercise(
            @AuthenticationPrincipal @NotNull User user,
            @Parameter(description = "ID do split") @PathVariable @NotNull UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable @NotNull UUID workoutId,
            @Parameter(description = "ID do exercício") @PathVariable @NotNull UUID exerciseId,
            @Valid @RequestBody AddExerciseRequest request) {

        var exercise = exerciseService.updateExercise(user.getId(), splitId, workoutId, exerciseId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new AddExerciseResponse(
                exercise.exerciseName(),
                exercise.workoutName(),
                exercise.Username(),
                exercise.exercisePosition()));
    }

    @Operation(summary = "Reordenar exercícios", description = "Atualiza a posição de múltiplos exercícios de um treino.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Posições atualizadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Exercício não pertence ao treino",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderExercises(
            @AuthenticationPrincipal @NotNull User user,
            @PathVariable @NotNull UUID workoutId,
            @RequestBody @NotNull ReorderExercisesRequest request) {

        exerciseService.reorderExercises(user.getId(), workoutId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar exercício", description = "Remove o exercício e reordena as posições dos demais no treino.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercício deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Exercício não encontrado ou não pertence ao usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(
            @AuthenticationPrincipal @NotNull User user,
            @Parameter(description = "ID do exercício") @PathVariable @NotNull UUID exerciseId) {

        boolean deleted = exerciseService.deleteExercise(user.getId(), exerciseId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Buscar detalhes de um exercício", description = "Retorna dados do exercício incluindo peso atual e reps do último top set.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercício encontrado e retornado"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Exercício não encontrado ou não pertence ao usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{exerciseId}")
    public ResponseEntity<GetExerciseDetailsResponse> getExercise(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do exercício") @PathVariable UUID exerciseId) {

        Optional<GetExerciseDetailsResponse> exercise = exerciseService.getExercise(user.getId(), exerciseId);
        if (exercise.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new GetExerciseDetailsResponse(
                    exercise.get().name(),
                    exercise.get().workoutName(),
                    exercise.get().sets(),
                    exercise.get().maxWeight(),
                    exercise.get().lastTopReps(),
                    exercise.get().positionInWorkout()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "Histórico de sets do exercício",
            description = "Retorna todos os sets já registrados para este exercício em todas as sessões, ordenados por data decrescente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado (lista vazia se nenhum set registrado)"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Exercício não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/exerciselog/{exerciseId}")
    public ResponseEntity<List<ExerciseLogDetailsResponse>> getExerciseLogs(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do exercício") @PathVariable UUID exerciseId) {

        Optional<List<ExerciseLogDetailsResponse>> exerciseLogs = exerciseService.getExerciseLogs(user.getId(), exerciseId);
        return ResponseEntity.status(HttpStatus.OK).body(exerciseLogs.orElse(List.of()));
    }
}
