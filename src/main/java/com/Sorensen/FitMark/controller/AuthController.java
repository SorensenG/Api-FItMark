package com.Sorensen.FitMark.controller;

import com.Sorensen.FitMark.config.security.JWT.TokenConfig;
import com.Sorensen.FitMark.dto.auth.*;
import com.Sorensen.FitMark.dto.error.ApiError;
import com.Sorensen.FitMark.entity.RefreshToken;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.service.RefreshTokenService;
import com.Sorensen.FitMark.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Auth", description = "Registro, login, renovação de token e logout")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService service;
    private final RefreshTokenService refreshTokenService;
    private final TokenConfig tokenConfig;

    public AuthController(UserService service, RefreshTokenService refreshTokenService, TokenConfig tokenConfig) {
        this.service = service;
        this.refreshTokenService = refreshTokenService;
        this.tokenConfig = tokenConfig;
    }

    @Operation(summary = "Registrar novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos (username, email ou password ausentes/mal formatados)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        var user = service.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponse(user.username(), user.email()));
    }

    @Operation(summary = "Autenticar usuário", description = "Retorna accessToken (30 min) e refreshToken (30 dias)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Email ou senha incorretos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campos inválidos (email ou password ausentes)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest login) {
        var loginResponse = service.login(login.email(), login.password());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "Renovar access token",
            description = "Valida o refreshToken, revoga-o e emite um novo par de tokens (rotação). " +
                    "Não requer Authorization header."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido, expirado ou já revogado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campo refreshToken ausente ou em branco",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken validated = refreshTokenService.validateRefreshToken(request.refreshToken());
        refreshTokenService.revokeRefreshToken(request.refreshToken());

        User user = validated.getUser();
        String newAccessToken = tokenConfig.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshToken.getToken()));
    }

    @Operation(
            summary = "Encerrar sessão",
            description = "Revoga o refreshToken informado. O accessToken continuará válido até expirar naturalmente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout realizado, refreshToken revogado"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Campo refreshToken ausente ou em branco",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request, @AuthenticationPrincipal User user) {
        refreshTokenService.revokeRefreshToken(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Dados do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Access token ausente ou inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> getUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userDetails = service.getUserDetails(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(userDetails);
    }
}