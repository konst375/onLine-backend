package com.chirko.onLine.common.event;

import com.chirko.onLine.domain.user.entity.User;
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

//    public SimpleMailMessage setTextAndSubjectForMail(SimpleMailMessage mail, String param) {
//        mail.setSubject("Default notification");
//        mail.setText("Hello from OnLine community!");
//        return mail;
//    }
}
