package com.chirko.onLine.service.user.event;

import com.chirko.onLine.entity.User;
import com.chirko.onLine.service.common.AbstractEvent;
import org.springframework.mail.SimpleMailMessage;

public class OnPasswordResetRequestEvent extends AbstractEvent {
    public OnPasswordResetRequestEvent(User source) {
        super(source);
    }

    @Override
    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail, String param) {
        mail.setSubject("Password reset request");
        final String confirmationUrl = String.format("/api/v1/user/resetPassword?token=%s", param);
        mail.setText("If the request is sent by you, please click on the link" +
                "\r\n" + "http://localhost:8080" + confirmationUrl);
        return mail;
    }
}
