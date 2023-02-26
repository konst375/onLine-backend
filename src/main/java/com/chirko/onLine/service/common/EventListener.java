package com.chirko.onLine.service.common;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.service.token.TokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventListener implements ApplicationListener<AbstractEvent> {
    private final TokenService tokenService;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void onApplicationEvent(@NonNull AbstractEvent event) {
        sendEmail(event);
    }

    private void sendEmail(AbstractEvent event) {
        User user = event.getUser();
        String token = tokenService.generateAccessToken(user);
        String recipientAddress = user.getEmail();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(recipientAddress);

        mailSender.send(event.setTextAndSubjectForMail(mail, token));
    }
}
