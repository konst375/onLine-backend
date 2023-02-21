package com.chirko.onLine.common.registration.event;

import com.chirko.onLine.entity.User;

public class OnResendingConfirmationLinkEvent extends AbstractRegistrationEvent {
    public OnResendingConfirmationLinkEvent(User source) {
        super(source);
    }
}
