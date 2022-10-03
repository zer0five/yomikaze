package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.ListVote;
import org.springframework.data.repository.CrudRepository;

public interface ListVoteRepository extends CrudRepository<ListVote, Integer> {
}