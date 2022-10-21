package org.yomikaze.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UsernameNotExistsConstraintValidator.class)
public @interface UsernameNotExistsConstraint {
    String message() default "Username already exists";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
