package com.Sorensen.FitMark.service;

import com.Sorensen.FitMark.entity.PasswordResetToken;
import com.Sorensen.FitMark.entity.User;
import com.Sorensen.FitMark.repository.PasswordResetTokenRepository;
import com.Sorensen.FitMark.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final int CODE_EXPIRY_MINUTES = 15;
    private static final int MAX_ATTEMPTS = 5;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void requestReset(String email) {
        Optional<UserDetails> userDetails = userRepository.findUserByEmail(email);

        // Não expõe se o e-mail existe ou não
        if (userDetails.isEmpty()) return;

        User user = (User) userDetails.get();

        // Remove códigos anteriores do usuário
        tokenRepository.deleteAllByUserId(user.getId());

        // Gera código numérico de 6 dígitos
        int rawCode = 100000 + secureRandom.nextInt(900000);
        String code = String.valueOf(rawCode);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setCode(code);
        resetToken.setExpiresAt(OffsetDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        tokenRepository.save(resetToken);

        emailService.sendPasswordResetCode(email, code);
    }

    @Transactional
    public void verifyCodeAndReset(String email, String code, String newPassword) {
        Optional<UserDetails> userDetails = userRepository.findUserByEmail(email);

        if (userDetails.isEmpty()) {
            throw new IllegalArgumentException("Código inválido ou expirado");
        }

        User user = (User) userDetails.get();

        PasswordResetToken resetToken = tokenRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Nenhum código solicitado"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Código já utilizado");
        }

        if (OffsetDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new IllegalArgumentException("Código expirado. Solicite um novo");
        }

        if (resetToken.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Muitas tentativas incorretas. Solicite um novo código");
        }

        if (!resetToken.getCode().equals(code)) {
            resetToken.setAttempts(resetToken.getAttempts() + 1);
            tokenRepository.save(resetToken);
            int remaining = MAX_ATTEMPTS - resetToken.getAttempts();
            throw new IllegalArgumentException(
                    remaining > 0
                            ? "Código incorreto. " + remaining + " tentativa(s) restante(s)"
                            : "Muitas tentativas incorretas. Solicite um novo código"
            );
        }

        // Código correto — atualiza senha e invalida o token
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
