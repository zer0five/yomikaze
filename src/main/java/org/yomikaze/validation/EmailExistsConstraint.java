package org.yomikaze.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmailExistsConstraintValidator.class)
public @interface EmailExistsConstraint {
    String message() default "Email does not exist";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
