package com.Sorensen.FitMark.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetCode(String toEmail, String code) {
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
    }
}
