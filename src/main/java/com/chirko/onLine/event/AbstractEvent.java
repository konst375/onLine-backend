package com.chirko.onLine.event;

import com.chirko.onLine.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.mail.SimpleMailMessage;

@Getter
@Setter
public abstract class AbstractEvent extends ApplicationEvent {
    private User user;

    public AbstractEvent(User source) {
        super(source);
        this.user = source;
    }

    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail) {
        mail.setSubject("Default notification");
        mail.setText("Hello from OnLine community!");
        return mail;
    }
}
