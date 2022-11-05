package org.yomikaze.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yomikaze.persistence.entity.Comment;
import org.yomikaze.snowflake.Snowflake;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Snowflake> {
    long countByChapterId(Snowflake chapterId);

    long countByComicId(Snowflake comicId);

    long countByAccountId(Snowflake accountId);

    @Query("select c.likedBy.size from comment c where c.id = ?1")
    int countLikesById(Snowflake commentId);

    Page<Comment> findAllByChapterId(Snowflake chapterId, Pageable pageable);

    Page<Comment> findAllByComicId(Snowflake comicId, Pageable pageable);
}
