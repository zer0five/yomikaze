package org.yomikaze.persistence.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Profile;
import org.yomikaze.persistence.entity.Role;
import org.yomikaze.persistence.repository.RoleRepository;

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
