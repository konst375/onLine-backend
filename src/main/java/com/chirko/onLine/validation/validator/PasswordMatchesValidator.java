package com.chirko.onLine.validation.validator;

import com.chirko.onLine.dto.request.RegisterRequestDto;
import com.chirko.onLine.dto.request.ResetUserPasswordDto;
import com.chirko.onLine.dto.request.UpdatePasswordDto;
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
        } else if (obj instanceof UpdatePasswordDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        }
        return false;
    }
}