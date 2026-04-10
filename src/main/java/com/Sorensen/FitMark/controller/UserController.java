package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.error.ApiError;
import com.Sorensen.FitMark.dto.user.ListUserWorkoutsResponse;
import com.Sorensen.FitMark.dto.user.UpdateProfilePhotoRequest;
import com.Sorensen.FitMark.dto.workout.ActiveSessionResponse;
import com.Sorensen.FitMark.dto.workout.AbandonSessionResponse;
import com.Sorensen.FitMark.dto.workout.ListAllSessionsResponse;
import com.Sorensen.FitMark.dto.workout.SessionDetailsResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.UserService;
import com.Sorensen.FitMark.service.WorkoutService;
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

import java.util.List;
import java.util.UUID;

@Tag(name = "Users", description = "Endpoints de consulta e ações do usuário autenticado")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final WorkoutSessionService workoutSessionService;

    public UserController(UserService userService, WorkoutService workoutService, WorkoutSessionService workoutSessionService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.workoutSessionService = workoutSessionService;
    }

    @Operation(summary = "Listar todos os treinos do usuário", description = "Retorna todos os treinos criados, independente do split.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/workouts")
    public ResponseEntity<ListUserWorkoutsResponse> listUserWorkouts(@AuthenticationPrincipal User user) {
        if (user == null || user.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var res = workoutService.listWorkout(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ListUserWorkoutsResponse(res.workouts(), res.totalWorkouts()));
    }

    @Operation(summary = "Buscar sessão ativa do usuário", description = "Retorna a sessão em andamento (completed=false e abandoned=false), ou 204 se não houver nenhuma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão ativa encontrada"),
            @ApiResponse(responseCode = "204", description = "Nenhuma sessão ativa"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/sessions/active")
    public ResponseEntity<ActiveSessionResponse> getActiveSession(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return workoutSessionService.getActiveSession(user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "Listar sessões concluídas do usuário", description = "Retorna apenas sessões com status completed=true, ordenadas por data decrescente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/sessions")
    public ResponseEntity<List<ListAllSessionsResponse>> listAllSessions(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ListAllSessionsResponse> response = workoutSessionService.listAllSessions(user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar detalhes de uma sessão", description = "Retorna a sessão com todos os sets registrados, agrupados por exercício.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão encontrada e retornada"),
            @ApiResponse(responseCode = "400", description = "Sessão não pertence ao usuário autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDetailsResponse> getSessionDetails(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID da sessão") @PathVariable UUID sessionId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SessionDetailsResponse response = workoutSessionService.getSessionDetails(user.getId(), sessionId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Abandonar sessão ativa",
            description = "Marca a sessão como abandonada (abandoned=true). A sessão não aparecerá no histórico de sessões concluídas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão abandonada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sessão não pertence ao usuário autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Sessão já foi concluída ou já está abandonada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/sessions/{sessionId}/abandon")
    public ResponseEntity<AbandonSessionResponse> abandonSession(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID da sessão") @PathVariable UUID sessionId) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AbandonSessionResponse response = workoutSessionService.abandonSession(user.getId(), sessionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar foto de perfil", description = "Define ou atualiza a URL da foto de perfil do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/profile-photo")
    public ResponseEntity<Void> updateProfilePhoto(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfilePhotoRequest request) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.updateProfilePhoto(user.getId(), request.profilePhotoUrl());
        return ResponseEntity.ok().build();
    }
}
