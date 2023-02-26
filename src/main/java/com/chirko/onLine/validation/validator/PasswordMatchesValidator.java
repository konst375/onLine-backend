package com.chirko.onLine.validation.validator;

import com.chirko.onLine.service.registration.dto.RegisterRequestDto;
import com.chirko.onLine.service.user.dto.ResetUserPasswordDto;
import com.chirko.onLine.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RegisterRequestDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        } else if (obj instanceof ResetUserPasswordDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        }
        return false;
    }
}