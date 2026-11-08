package com.hsm.admin.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** Admin-only email adapter. No changes to the account team's EmailService API. */
@Service
public class AdminMailService {
    private final JavaMailSender mailSender;
    private final String from;
    private final boolean enabled;

    public AdminMailService(JavaMailSender mailSender,
                            @Value("${app.mail.from:no-reply@hsm.local}") String from,
                            @Value("${app.mail-enabled:false}") boolean enabled) {
        this.mailSender = mailSender;
        this.from = from;
        this.enabled = enabled;
    }

    public void sendNotification(String recipient, String subject, String body) {
        if (!enabled || recipient == null || recipient.isBlank()) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
