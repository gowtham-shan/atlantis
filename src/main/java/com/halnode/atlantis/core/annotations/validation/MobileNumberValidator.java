package com.halnode.atlantis.core.annotations.validation;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MobileNumberValidator implements ConstraintValidator<ValidMobileNumber, Long> {

    @Override
    public void initialize(ValidMobileNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        String mobileNumber = String.valueOf(value);
        Pattern P = Pattern.compile("(6-9)?[6-9][0-9]{9}");
        Matcher m = P.matcher(mobileNumber);
        return (m.find() && m.group().equals(mobileNumber));
    }
}
