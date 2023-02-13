package com.chirko.onLine.validation;

import com.chirko.onLine.dto.RegisterRequest;
import com.chirko.onLine.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        RegisterRequest request = (RegisterRequest) obj;
        return request.getPassword().equals(request.getMatchingPassword());
    }
}