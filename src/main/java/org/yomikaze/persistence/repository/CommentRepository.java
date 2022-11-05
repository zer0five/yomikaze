package org.yomikaze.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comment;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Snowflake> {

    Optional<Comment> findCommentByAccount(Account account);


}
