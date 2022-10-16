package io.gitlab.zeromatter.yomikaze.persistence.listener;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Profile;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Role;
import io.gitlab.zeromatter.yomikaze.persistence.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PrePersist;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountListener {
    private static RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        AccountListener.roleRepository = roleRepository;
    }

    @PrePersist
    @Transactional
    public void onCreate(Account account) {
        if (account.getRoles() == null || account.getRoles().isEmpty()) {
            Collection<Role> defaultRoles = roleRepository.findAllByDefaultRoleIsTrue();
            account.setRoles(defaultRoles);
        }
        Profile profile = account.getProfile();
        if (profile.getDisplayName() == null || profile.getDisplayName().isEmpty()) {
            profile.setDisplayName(account.getUsername());
        }
    }
}
