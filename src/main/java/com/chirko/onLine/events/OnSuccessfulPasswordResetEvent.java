package com.chirko.onLine.events;

import com.chirko.onLine.entities.User;
import org.springframework.mail.SimpleMailMessage;

public class OnSuccessfulPasswordResetEvent extends AbstractEvent {
    public OnSuccessfulPasswordResetEvent(User source) {
        super(source);
    }

    @Override
    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail) {
        mail.setSubject("Password reset");
        mail.setText("Password successful reset");
        return mail;
    }
}
