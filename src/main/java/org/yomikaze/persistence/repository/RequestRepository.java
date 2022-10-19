package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Request;
import org.yomikaze.snowflake.Snowflake;

@Repository
public interface RequestRepository extends CrudRepository<Request, Snowflake> {

    Page<Request> findAllByRequester(Account requester, Pageable pageable);

    Page<Request> findAllByApprovedIsNull(Pageable pageable);


}
