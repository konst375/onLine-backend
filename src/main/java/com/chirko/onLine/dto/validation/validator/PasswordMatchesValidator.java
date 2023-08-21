package com.chirko.onLine.dto.validation.validator;

import com.chirko.onLine.dto.request.user.RegisterUserRequestDto;
import com.chirko.onLine.dto.request.user.ResetUserPasswordRequestDto;
import com.chirko.onLine.dto.request.user.UpdateUserPasswordRequestDto;
import com.chirko.onLine.dto.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RegisterUserRequestDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        } else if (obj instanceof ResetUserPasswordRequestDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        } else if (obj instanceof UpdateUserPasswordRequestDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        }
        return false;
    }
}