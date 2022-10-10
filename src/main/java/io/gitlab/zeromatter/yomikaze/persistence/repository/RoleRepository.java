package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Role;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Snowflake> {
    Optional<Role> findByName(String name);

    Collection<Role> findAllByDefaultRoleIsTrue();
}