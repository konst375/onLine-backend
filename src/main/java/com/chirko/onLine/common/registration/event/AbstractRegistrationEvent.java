package com.chirko.onLine.common.registration.event;

import com.chirko.onLine.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public abstract class AbstractRegistrationEvent extends ApplicationEvent {
    private User user;

    public AbstractRegistrationEvent(User source) {
        super(source);
        this.user = source;
    }
}
