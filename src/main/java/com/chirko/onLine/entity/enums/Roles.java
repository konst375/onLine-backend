package com.chirko.onLine.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.NonNull;

import java.util.stream.Stream;

public enum Roles {
    ADMIN,
    CREATOR,
    USER;

    @Converter
    public static class RoleConverter implements AttributeConverter<Roles, String> {
        @Override
        public String convertToDatabaseColumn(@NonNull Roles role) {
            return role.name();
        }

        @Override
        public Roles convertToEntityAttribute(@NonNull String roleFromDB) {
            return Stream.of(Roles.values())
                    .filter(role -> role.name().equals(roleFromDB))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}