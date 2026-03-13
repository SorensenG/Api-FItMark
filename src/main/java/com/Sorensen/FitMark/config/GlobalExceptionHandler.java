package com.Sorensen.FitMark.config;

import com.Sorensen.FitMark.dto.error.ApiError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Entidade não encontrada, pertence a outro usuário, parâmetro inválido
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(400, "Bad Request", ex.getMessage()));
    }

    // Estado inválido: sessão já finalizada, etc.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(409, "Conflict", ex.getMessage()));
    }

    // Violação de constraint do banco: série duplicada, email já cadastrado, etc.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = resolveDataIntegrityMessage(ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(409, "Conflict", message));
    }

    // Falha de validação nos campos do body (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> "'" + fe.getField() + "': " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiError(422, "Unprocessable Entity", "Validation failed", details));
    }

    // Credenciais inválidas no login
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<ApiError> handleBadCredentials(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(401, "Unauthorized", "Invalid email or password"));
    }

    // Catch-all — nunca deve vazar detalhes internos
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(500, "Internal Server Error", "An unexpected error occurred"));
    }

    private String resolveDataIntegrityMessage(DataIntegrityViolationException ex) {
        String cause = ex.getMostSpecificCause().getMessage();
        if (cause == null) return "Data integrity violation";

        if (cause.contains("uq_set_logs_session_exercise_setnum")) {
            return "Set number already logged for this exercise in this session";
        }
        if (cause.contains("uq_splits_user_name")) {
            return "A split with this name already exists for this user";
        }
        if (cause.contains("users_email_key") || cause.contains("email")) {
            return "Email already registered";
        }
        return "Data integrity violation";
    }
}
