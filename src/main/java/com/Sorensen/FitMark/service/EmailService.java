package com.Sorensen.FitMark.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendPasswordResetCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("FitMark — Código de redefinição de senha");
            message.setText("""
                    Olá,

                    Recebemos uma solicitação para redefinir a senha da sua conta FitMark.

                    Seu código de verificação é:

                    %s

                    O código é válido por 15 minutos. Não compartilhe com ninguém.

                    Se você não solicitou a redefinição, ignore este e-mail.

                    — Equipe FitMark
                    """.formatted(code));

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail de redefinição para {}", toEmail, ex);
        }
    }
}
