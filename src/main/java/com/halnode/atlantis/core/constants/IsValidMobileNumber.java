package com.halnode.atlantis.core.constants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileNumberValidator.class)
@Documented
public @interface IsValidMobileNumber {

    String message() default "Mobile Number is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
