package org.yomikaze.validation;

import lombok.Data;
import org.yomikaze.persistence.repository.AccountRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Data
public class UsernameNotExistsConstraintValidator implements ConstraintValidator<UsernameNotExistsConstraint, String> {

    private final AccountRepository accountRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !accountRepository.findByUsernameOrEmail(value, value).isPresent();
    }
}
