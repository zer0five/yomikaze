package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Permission;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Snowflake> {

    Optional<Permission> findByAuthority(String authority);

}
