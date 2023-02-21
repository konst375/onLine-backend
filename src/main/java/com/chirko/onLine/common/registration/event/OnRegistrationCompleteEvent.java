package com.chirko.onLine.common.registration.event;

import com.chirko.onLine.entity.User;

public class OnRegistrationCompleteEvent extends AbstractRegistrationEvent {
    public OnRegistrationCompleteEvent(User source) {
        super(source);
    }
}
