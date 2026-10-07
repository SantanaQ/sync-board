package com.syncboard.common.validation;

import com.syncboard.auth.api.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConfirmationValidator
        implements ConstraintValidator<PasswordConfirmation, RegisterRequest> {

    @Override
    public void initialize(PasswordConfirmation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(
            RegisterRequest request,
            ConstraintValidatorContext context
    ) {

       if(request == null) {
           return true;
       }

        if (request.password() == null
                || request.passwordConfirmation() == null
                || request.password().isBlank()
                || request.passwordConfirmation().isBlank()) {
            return true;
        }


        if (!request.password().equals(request.passwordConfirmation())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            context.getDefaultConstraintMessageTemplate()
                    )
                    .addPropertyNode("passwordConfirmation")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
