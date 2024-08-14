package com.sonarcube.eighty.util.annotation;

import com.sonarcube.eighty.exception.InvalidRequestException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OnlyBooleanValidator implements ConstraintValidator<OnlyBoolean, Boolean>{
    @Override
    public void initialize(OnlyBoolean constraintAnnotation) {
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        return value != null;
    }
}
