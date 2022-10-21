package org.yomikaze.validation;

import lombok.Data;
import org.yomikaze.persistence.repository.AccountRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Data
public class UsernameExistsConstraintValidator implements ConstraintValidator<UsernameExistsConstraint, String> {

        private final AccountRepository accountRepository;
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return false;
        }
}
