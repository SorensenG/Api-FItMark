package com.Sorensen.FitMark.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestClient brevoRestClient;

    @Value("${MAIL_FROM}")
    private String fromEmail;

    @Value("${MAIL_FROM_NAME:FitMark}")
    private String fromName;

    public EmailService(@Qualifier("brevoRestClient") RestClient brevoRestClient) {
        this.brevoRestClient = brevoRestClient;
    }

    @Async
    public void sendPasswordResetCode(String toEmail, String code) {
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", fromName, "email", fromEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "FitMark — Código de redefinição de senha",
                "textContent", """
                        Olá,

                        Recebemos uma solicitação para redefinir a senha da sua conta FitMark.

                        Seu código de verificação é:

                        %s

                        O código é válido por 15 minutos. Não compartilhe com ninguém.

                        Se você não solicitou a redefinição, ignore este e-mail.

                        — Equipe FitMark
                        """.formatted(code)
        );

        try {
            brevoRestClient.post()
                    .uri("/smtp/email")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de redefinição para {}: {}", toEmail, e.getMessage());
        }
    }
}
