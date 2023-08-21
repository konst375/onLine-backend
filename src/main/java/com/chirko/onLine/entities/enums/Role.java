package com.chirko.onLine.entities.enums;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public enum Role implements GrantedAuthority {
    ADMIN,
    STAFF,
    USER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}