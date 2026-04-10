package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.error.ApiError;
import com.Sorensen.FitMark.dto.workout.*;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.WorkoutSessionService;
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

@Tag(name = "Workout Sessions", description = "Ciclo de vida de uma sessão de treino: iniciar → registrar séries → finalizar/abandonar")
@RestController
@RequestMapping("/splits/{splitId}/workouts/{workoutId}")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;

    public WorkoutSessionController(WorkoutSessionService workoutSessionService) {
        this.workoutSessionService = workoutSessionService;
    }

    @Operation(
            summary = "Iniciar sessão de treino",
            description = "Cria uma nova sessão ativa para o treino. Só é possível ter uma sessão ativa por vez."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão iniciada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Treino não pertence ao usuário, ou workout não pertence ao split informado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Já existe uma sessão ativa em andamento — finalize ou abandone antes de iniciar outra",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/workoutsession-start")
    public ResponseEntity<StartWorkOutSessionResponse> startWorkoutSession(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do treino") @PathVariable UUID workoutId,
            @Parameter(description = "ID do split") @PathVariable UUID splitId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var response = workoutSessionService.startWorkoutSession(user.getId(), splitId, workoutId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Registrar série",
            description = "Registra uma série executada durante a sessão ativa. " +
                    "setType aceita: WORK, WARMUP, DROP, FAILURE, BACKOFF, AMRAP, REST_PAUSE, SUPERSET."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Série registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não pertence ao usuário, ou exercício não pertence ao treino desta sessão",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sessão ou exercício não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Sessão já concluída — não é possível registrar séries em sessões finalizadas",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/sessions/{sessionId}/sets")
    public ResponseEntity<LogSetResponse> logSet(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable UUID workoutId,
            @Parameter(description = "ID da sessão ativa") @PathVariable UUID sessionId,
            @Valid @RequestBody LogSetRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var response = workoutSessionService.logSet(user.getId(), sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Editar série registrada",
            description = "Edita uma série já registrada na sessão ativa. Não é permitido editar séries de sessões finalizadas ou abandonadas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Série atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Série não pertence a esta sessão",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sessão já finalizada ou abandonada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sessão ou série não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/sessions/{sessionId}/sets/{setId}")
    public ResponseEntity<UpdateSetLogResponse> updateSetLog(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable UUID workoutId,
            @Parameter(description = "ID da sessão ativa") @PathVariable UUID sessionId,
            @Parameter(description = "ID da série") @PathVariable UUID setId,
            @Valid @RequestBody UpdateSetLogRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var response = workoutSessionService.updateSetLog(user.getId(), sessionId, setId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Finalizar sessão de treino",
            description = "Marca a sessão como concluída, salva duração e notas, e atualiza o cache de peso/reps de cada exercício."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão finalizada com sucesso, retorna resumo por exercício"),
            @ApiResponse(responseCode = "400", description = "Sessão não pertence ao usuário autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Sessão já foi finalizada anteriormente",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/sessions/{sessionId}/finish")
    public ResponseEntity<FinishSessionResponse> finishSession(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Parameter(description = "ID do treino") @PathVariable UUID workoutId,
            @Parameter(description = "ID da sessão") @PathVariable UUID sessionId,
            @RequestBody FinishSessionRequest request) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var response = workoutSessionService.finishSession(user.getId(), sessionId, request);
        return ResponseEntity.ok(response);
    }
}
