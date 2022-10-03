package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.ListItem;
import org.springframework.data.repository.CrudRepository;

public interface ListItemRepository extends CrudRepository<ListItem, Integer> {
}