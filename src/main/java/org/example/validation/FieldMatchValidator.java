package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanUtils;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object firstValue = BeanUtils.getPropertyDescriptor(
                    value.getClass(), field).getReadMethod().invoke(value);
            Object secondValue = BeanUtils.getPropertyDescriptor(
                    value.getClass(), fieldMatch).getReadMethod().invoke(value);

            return firstValue != null && firstValue.equals(secondValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
