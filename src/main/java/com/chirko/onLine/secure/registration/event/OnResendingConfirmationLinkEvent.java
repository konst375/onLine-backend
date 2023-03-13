package com.chirko.onLine.secure.registration.event;

import com.chirko.onLine.common.event.AbstractEvent;
import com.chirko.onLine.user.entity.User;
import lombok.Getter;
import org.springframework.mail.SimpleMailMessage;

@Getter
public class OnResendingConfirmationLinkEvent extends AbstractEvent {

    private final String token;

    public OnResendingConfirmationLinkEvent(User source, String token) {
        super(source);
        this.token = token;
    }

    @Override
    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail) {
        mail.setSubject("Registration Confirmation");
        final String confirmationUrl = String.format("/api/v1/registration/confirm?token=%s", token);
        mail.setText("Registration success, please confirm your email" +
                "\r\n" + "http://localhost:8080" + confirmationUrl);
        return mail;
    }
}
