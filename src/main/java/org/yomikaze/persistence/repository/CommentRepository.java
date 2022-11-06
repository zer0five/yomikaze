package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comment;
import org.yomikaze.snowflake.Snowflake;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Snowflake> {
    long countByChapterId(Snowflake chapterId);

    Optional<Comment> findCommentByAccount(Account account);
    long countByComicId(Snowflake comicId);

    long countByAccountId(Snowflake accountId);

    @Query("select c.likedBy.size from comment c where c.id = ?1")
    int countLikesById(Snowflake commentId);

    Page<Comment> findAllByChapterId(Snowflake chapterId, Pageable pageable);

    Page<Comment> findAllByComicIdAndParentIsNull(Snowflake comicId, Pageable pageable);
}
