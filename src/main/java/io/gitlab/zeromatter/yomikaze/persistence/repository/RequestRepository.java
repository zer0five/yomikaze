package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Request;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Collection;

@Repository
public interface RequestRepository extends CrudRepository<Request, Snowflake> {

    Page<Request> findAllByRequester(Account requester,Pageable pageable) ;


}