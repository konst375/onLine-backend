package com.chirko.onLine.dto.validation.validator;

import com.chirko.onLine.dto.validation.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.StringJoiner;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator passwordValidator = new PasswordValidator(
                Arrays.asList(
                        new LengthRule(8, 30),
                        new CharacterRule(EnglishCharacterData.LowerCase, 1),
                        new CharacterRule(EnglishCharacterData.UpperCase, 1),
                        new CharacterRule(EnglishCharacterData.Digit, 1),
                        new CharacterRule(EnglishCharacterData.Special, 1),
                        new CharacterOccurrencesRule(5),
                        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                        new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                        new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                        new WhitespaceRule()
                ));

        RuleResult result = passwordValidator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        StringJoiner joiner = new StringJoiner(", ");
        passwordValidator.getMessages(result).forEach(joiner::add);

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(joiner.toString()).addConstraintViolation();
        return false;
    }
}
