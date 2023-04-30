package com.chirko.onLine.dto.validation.validator;

import com.chirko.onLine.dto.request.user.RQRegisterUserDto;
import com.chirko.onLine.dto.request.user.RQResetUserPasswordDto;
import com.chirko.onLine.dto.request.user.RQUpdateUserPasswordDto;
import com.chirko.onLine.dto.validation.annotation.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RQRegisterUserDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        } else if (obj instanceof RQResetUserPasswordDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        } else if (obj instanceof RQUpdateUserPasswordDto request) {
            return request.getPassword().equals(request.getMatchingPassword());
        }
        return false;
    }
}