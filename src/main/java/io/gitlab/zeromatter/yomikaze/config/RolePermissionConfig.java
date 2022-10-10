package io.gitlab.zeromatter.yomikaze.config;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Permission;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Role;
import io.gitlab.zeromatter.yomikaze.persistence.repository.PermissionRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Data
@Component
public class RolePermissionConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    @Setter(AccessLevel.PRIVATE)
    private boolean initialized = false;

    @Transactional
    public Permission createPermissionIfNotFound(String name) {
        return permissionRepository
                .findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
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
        if (isInitialized()) {
            return;
        }
        String user = "User";
        List<Permission> userPermissions = Arrays.asList(
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
        createRoleIfNotFound(user, userPermissions, true);

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
        createRoleIfNotFound(uploader, uploaderPermissions);
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
        createRoleIfNotFound(admin, adminPermissions);
        setInitialized(true);
    }
}
