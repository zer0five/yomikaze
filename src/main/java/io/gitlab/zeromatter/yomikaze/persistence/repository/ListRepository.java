package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.List;
import org.springframework.data.repository.CrudRepository;

public interface ListRepository extends CrudRepository<List, Integer> {
}