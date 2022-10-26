package org.yomikaze.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yomikaze.persistence.entity.*;
import org.yomikaze.persistence.repository.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Component
public class RolePermissionConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private boolean initialized = false;

    @Transactional
    public Permission createPermissionIfNotFound(String authority) {
        return permissionRepository
            .findByAuthority(authority)
            .orElseGet(() -> {
                Permission permission = new Permission();
                permission.setAuthority(authority);
                return permissionRepository.save(permission);
            });
    }

    @Transactional
    public Role createRoleIfNotFound(String name, Collection<Permission> privileges) {
        return createRoleIfNotFound(name, privileges, false);
    }

    @Transactional
    public Role createRoleIfNotFound(String name, Collection<Permission> privileges, boolean defaultRole) {
        return roleRepository
            .findByName(name)
            .orElseGet(() -> {
                Role role = new Role();
                role.setName(name);
                role.setPermissions(privileges);
                role.setDefaultRole(defaultRole);
                return roleRepository.save(role);
            });
    }


    @Override
    @Transactional
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        if (isInitialized()) return;

        String member = "Member";
        List<Permission> memberPermissions = Arrays.asList(
            createPermissionIfNotFound("comic.search.advanced"),
            createPermissionIfNotFound("history.record.comic"),
            createPermissionIfNotFound("history.record.chapter"),
            createPermissionIfNotFound("history.record.page"),
            createPermissionIfNotFound("history.delete"),
            createPermissionIfNotFound("history.delete.all"),
            createPermissionIfNotFound("library.add"),
            createPermissionIfNotFound("library.delete"),
            createPermissionIfNotFound("library.delete.all"),
            createPermissionIfNotFound("library.organize"),
            createPermissionIfNotFound("request.create.uploader"),
            createPermissionIfNotFound("request.cancel"),
            createPermissionIfNotFound("request.cancel.all"),
            createPermissionIfNotFound("report.create")
        );
        Role memberRole = createRoleIfNotFound(member, memberPermissions, true);

        String uploader = "Uploader";
        List<Permission> uploaderPermissions = Arrays.asList(
            createPermissionIfNotFound("comic.create"),
            createPermissionIfNotFound("comic.update"),
            createPermissionIfNotFound("comic.delete"),
            createPermissionIfNotFound("chapter.create"),
            createPermissionIfNotFound("chapter.update"),
            createPermissionIfNotFound("chapter.delete"),
            createPermissionIfNotFound("page.create"),
            createPermissionIfNotFound("page.delete")
        );
        Role uploaderRole = createRoleIfNotFound(uploader, uploaderPermissions);

        String admin = "Admin";
        List<Permission> adminPermissions = Arrays.asList(
            createPermissionIfNotFound("request.approve"),
            createPermissionIfNotFound("request.approve.all"),
            createPermissionIfNotFound("request.reject"),
            createPermissionIfNotFound("request.reject.all"),
            createPermissionIfNotFound("report.resolve"),
            createPermissionIfNotFound("comic.update.other"),
            createPermissionIfNotFound("comic.delete.other"),
            createPermissionIfNotFound("chapter.update.other"),
            createPermissionIfNotFound("chapter.delete.other"),
            createPermissionIfNotFound("page.delete.other"),
            createPermissionIfNotFound("user.ban"),
            createPermissionIfNotFound("user.unban"),
            createPermissionIfNotFound("user.delete"),
            createPermissionIfNotFound("user.delete.all"),
            createPermissionIfNotFound("user.update")
        );
        Role adminRole = createRoleIfNotFound(admin, adminPermissions);
        Optional<Account> adminAccount = accountRepository.findByUsername("admin");
        if (adminAccount.isPresent()) {
            if (adminAccount.get().getProfile() == null) {
                Profile profile = new Profile();
                profile.setAccount(adminAccount.get());
                profile.setDisplayName("Administrator");
                adminAccount.get().setProfile(profile);
            }
        } else {
            Account account = new Account();
            account.setUsername("admin");
            account.setEmail("admin@yomikaze.org");
            account.setPassword(passwordEncoder.encode("admin"));
            account.addRole(memberRole);
            account.addRole(uploaderRole);
            account.addRole(adminRole);
            account.getProfile().setDisplayName("Administrator");
            accountRepository.save(account);
        }
        setInitialized(true);
    }
}
