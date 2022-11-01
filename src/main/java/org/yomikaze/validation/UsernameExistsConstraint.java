package org.yomikaze.validation;
import javax.validation.Constraint;
import java.lang.annotation.*;
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UsernameExistsConstraintValidator.class)
public @interface UsernameExistsConstraint {
    String message() default "Username does not exist";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
