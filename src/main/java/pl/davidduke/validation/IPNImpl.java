package pl.davidduke.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class IPNImpl implements ConstraintValidator<IPN, String> {
    final int[] PALINOM = new int[] {-1, 5, 7, 9, 4, 6, 10, 5, 7};
    @Override
    public void initialize(IPN constraintAnnotation) {
        // nothing to initialize
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.isBlank() || !s.matches("^[0-9]*$")) {
            return false;
        }
        int controlSum = 0;
        for (int i = 0; i < PALINOM.length; i++) {
            controlSum += PALINOM[i] * Integer.parseInt(String.valueOf(s.charAt(i)));
        }
        int controlNumber = controlSum % 11 % 10;
        return s.endsWith(String.valueOf(controlNumber));
    }
}
