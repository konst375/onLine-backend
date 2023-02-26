package com.chirko.onLine.service.user.event;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.service.common.AbstractEvent;
import org.springframework.mail.SimpleMailMessage;

public class OnSuccessfulPasswordResetEvent extends AbstractEvent {
    public OnSuccessfulPasswordResetEvent(User source) {
        super(source);
    }

    @Override
    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail, String param) {
        mail.setSubject("Password reset");
        mail.setText("Password successful reset");
        return mail;
    }
}
