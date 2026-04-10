package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.error.ApiError;
import com.Sorensen.FitMark.dto.workout.CreateWorkoutRequest;
import com.Sorensen.FitMark.dto.workout.WorkoutResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Workouts", description = "Gerenciamento de treinos dentro de um split")
@RestController
@RequestMapping("/splits/{splitId}/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @Operation(summary = "Criar treino em um split")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Treino criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário ou split não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campo title ausente ou em branco",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Valid @RequestBody CreateWorkoutRequest req) {

        var id = user.getId();
        if (id == null || splitId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var workout = workoutService.createWorkout(id, req, splitId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new WorkoutResponse(workout.id(), workout.userId(), workout.splitID(),
                        workout.userName(), workout.pos(), workout.title(), workout.exercises(), workout.notes()));
    }

    @Operation(summary = "Buscar detalhes de um treino")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino encontrado e retornado"),
            @ApiResponse(responseCode = "400", description = "Treino não encontrado ou não pertence ao usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponse> getWorkout(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable UUID workoutId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var workoutOpt = workoutService.getWorkout(user.getId(), workoutId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new WorkoutResponse(workoutOpt.id(), workoutOpt.userId(), workoutOpt.splitID(),
                        workoutOpt.userName(), workoutOpt.pos(), workoutOpt.title(), workoutOpt.exercises(), workoutOpt.notes()));
    }

    @Operation(summary = "Deletar um treino", description = "Remove o treino e todos os exercícios associados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado ou não pertence ao usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable UUID workoutId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean deleted = workoutService.deleteWorkout(user, workoutId);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Atualizar um treino", description = "Atualiza o título e as notas do treino.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Treino não encontrado ou não pertence ao usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponse> updateWorkout(
            @AuthenticationPrincipal User user,
            @PathVariable UUID splitId,
            @PathVariable UUID workoutId,
            @Valid @RequestBody CreateWorkoutRequest req) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var workout = workoutService.updateWorkout(user.getId(), workoutId, req);
        return ResponseEntity.ok(new WorkoutResponse(
                workout.id(), workout.userId(), workout.splitID(),
                workout.userName(), workout.pos(), workout.title(), workout.exercises(), workout.notes()));
    }
}
