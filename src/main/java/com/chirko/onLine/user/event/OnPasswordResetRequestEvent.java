package com.chirko.onLine.user.event;

import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.common.event.AbstractEvent;
import lombok.Getter;
import org.springframework.mail.SimpleMailMessage;

@Getter
public class OnPasswordResetRequestEvent extends AbstractEvent {
    private final String token;

    public OnPasswordResetRequestEvent(User source, String token) {
        super(source);
        this.token = token;
    }

    @Override
    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail) {
        mail.setSubject("Password reset request");
        final String confirmationUrl = String.format("/api/v1/user/password/reset?token=%s", token);
        mail.setText("If the request is sent by you, please click on the link" +
                "\r\n" + "http://localhost:8080" + confirmationUrl);
        return mail;
    }
}
