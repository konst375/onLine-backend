package com.chirko.onLine.validation;

import com.chirko.onLine.dto.RegisterRequestDto;
import com.chirko.onLine.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        RegisterRequestDto request = (RegisterRequestDto) obj;
        return request.getPassword().equals(request.getMatchingPassword());
    }
}