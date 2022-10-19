package org.yomikaze.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Permission;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Snowflake> {

    Optional<Permission> findByAuthority(String authority);

}
