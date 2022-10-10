package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Request;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends CrudRepository<Request, Integer> {

}