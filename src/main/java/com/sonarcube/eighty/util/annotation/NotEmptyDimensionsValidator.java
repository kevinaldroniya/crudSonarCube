package com.sonarcube.eighty.util.annotation;

import com.sonarcube.eighty.dto.Dimensions;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyDimensionsValidator implements ConstraintValidator<NotEmptyDimensions, Dimensions> {
    @Override
    public boolean isValid(Dimensions dimensions, ConstraintValidatorContext context) {
        if (dimensions == null) {
            return false;
        }
        return dimensions.getLength() > 0 || dimensions.getWidth() > 0 || dimensions.getHeight() > 0 || dimensions.getWeight() > 0;
    }
}
