package com.chirko.onLine.common.registration.event;

import com.chirko.onLine.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent{
    private User user;

    public OnRegistrationCompleteEvent(User source) {
        super(source);
        this.user = source;
    }
}
