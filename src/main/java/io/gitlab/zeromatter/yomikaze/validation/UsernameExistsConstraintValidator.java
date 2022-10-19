package io.gitlab.zeromatter.yomikaze.validation;

import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import lombok.Data;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Data
public class UsernameExistsConstraintValidator implements ConstraintValidator<UsernameExistsConstraint, String> {

    private final AccountRepository accountRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !accountRepository.findByUsername(value).isPresent();
    }
}
