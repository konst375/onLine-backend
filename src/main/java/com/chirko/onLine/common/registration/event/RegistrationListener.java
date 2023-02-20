package com.chirko.onLine.common.registration.event;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.service.TokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final TokenService tokenService;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void onApplicationEvent(@NonNull OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = tokenService.generateAccessToken(user);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = String.format("/api/v1/registration/registrationConfirm?token=%s", token);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(username);
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Registration success, please confirm your email" + "\r\n" + "http://localhost:8080" + confirmationUrl);

        mailSender.send(email);
    }
}
