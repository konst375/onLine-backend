package com.chirko.onLine.user.event;

import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.common.event.AbstractEvent;
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
