package com.chirko.onLine.service.registration.event;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.service.common.AbstractEvent;
import org.springframework.mail.SimpleMailMessage;

public class OnResendingConfirmationLinkEvent extends AbstractEvent {
    public OnResendingConfirmationLinkEvent(User source) {
        super(source);
    }

    @Override
    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail, String param) {
        mail.setSubject("Registration Confirmation");
        final String confirmationUrl = String.format("/api/v1/registration/registrationConfirm?token=%s", param);
        mail.setText("Registration success, please confirm your email" +
                "\r\n" + "http://localhost:8080" + confirmationUrl);
        return mail;
    }
}
