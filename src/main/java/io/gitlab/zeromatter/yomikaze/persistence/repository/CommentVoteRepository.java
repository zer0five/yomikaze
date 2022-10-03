package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.CommentVote;
import org.springframework.data.repository.CrudRepository;

public interface CommentVoteRepository extends CrudRepository<CommentVote, Integer> {
}