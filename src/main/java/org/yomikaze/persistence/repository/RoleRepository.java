package org.yomikaze.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Role;
import org.yomikaze.snowflake.Snowflake;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Snowflake> {
    Optional<Role> findByName(String name);

    Collection<Role> findAllByDefaultRoleIsTrue();
}
