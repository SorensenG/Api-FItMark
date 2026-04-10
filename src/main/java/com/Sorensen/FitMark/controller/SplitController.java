package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.dto.error.ApiError;
import com.Sorensen.FitMark.dto.split.SplitCreateRequest;
import com.Sorensen.FitMark.dto.split.SplitCreateResponse;
import com.Sorensen.FitMark.dto.split.SplitDetailsResponse;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.SplitService;
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

@Tag(name = "Splits", description = "Gerenciamento de divisões de treino (PPL, Upper/Lower, etc.)")
@RestController
@RequestMapping("/splits")
public class SplitController {

    private final SplitService splitService;

    public SplitController(SplitService splitService) {
        this.splitService = splitService;
    }

    @Operation(summary = "Listar splits do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de splits retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping()
    public ResponseEntity<List<SplitDetailsResponse>> getSplits(@AuthenticationPrincipal User user) {
        var userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var splits = splitService.getSplitsForUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(splits);
    }

    @Operation(summary = "Criar novo split")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Split criado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Já existe um split com este nome para o usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campo title ausente ou em branco",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<SplitCreateResponse> splitCreate(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SplitCreateRequest splitCreateRequest) {

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

    @Operation(summary = "Buscar detalhes de um split")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Split encontrado e retornado"),
            @ApiResponse(responseCode = "400", description = "Split não pertence ao usuário autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Split não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{splitId}")
    public ResponseEntity<SplitDetailsResponse> getSplitDetails(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId) {

        var userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var splitDetails = splitService.getSplitDetails(userId, splitId);
        if (splitDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(splitDetails);
    }

    @Operation(summary = "Renomear um split")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Split atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Split não pertence ao usuário autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Split não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Já existe um split com este nome para o usuário",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campo title ausente ou em branco",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{splitId}")
    public ResponseEntity<SplitDetailsResponse> updateSplit(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId,
            @Valid @RequestBody SplitCreateRequest request) {

        var userId = user.getId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var updated = splitService.updateSplit(userId, splitId, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Deletar um split", description = "Remove o split e todos os treinos associados a ele.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Split deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Split não pertence ao usuário autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Split não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("{splitId}")
    public ResponseEntity<Void> deleteSplit(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID do split") @PathVariable UUID splitId) {

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
