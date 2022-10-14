package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Comic;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Genre;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ComicRepository extends CrudRepository<Comic, Snowflake> {
    Page<Comic> findAll(Pageable pageable);

    @Query(
            // @formatter:off
            "select c " +
            "from #{#entityName} c " +
                "join c.genres g " +
            "where g in (:#{#genres}) " +
            "group by c " +
            "having count(c) = :#{#genres.size.longValue}"
            // @formatter:on
    )
    Page<Comic> findByGenresContainingAll(Collection<Genre> genres, Pageable pageable);

    @Query(
            // @formatter:off
            "select c " +
            "from #{#entityName} c " +
            "where c not in (" +
                "select c " +
                "from #{#entityName} c " +
                "join c.genres g " +
                "where g in (:#{#genres}) " +
                "group by c " +
                "having count(c) = :#{#genres.size.longValue} " +
            ")"
            // @formatter:on
    )
    Page<Comic> findByGenresContainingNone(Collection<Genre> genres, Pageable pageable);

    @Query(
            // @formatter:off
            "select c " +
            "from #{#entityName} c " +
            "where c not in (" +
                "select c " +
                "from #{#entityName} c " +
                    "join c.genres g " +
                "where g in (:#{#blacklist}) " +
                "group by c " +
                "having count(c) = :#{#blacklist.size.longValue} " +
            ") " +
            "and c in (" +
                "select c " +
                "from #{#entityName} c " +
                    "join c.genres g " +
                "where g in (:#{#whitelist}) " +
                "group by c " +
                "having count(c) = :#{#whitelist.size.longValue} " +
            ") " +
            "group by c "
            // @formatter:on
    )
    Page<Comic> findByGenresContainingAllAndGenresContainingNone(Collection<Genre> whitelist, Collection<Genre> blacklist, Pageable pageable);

    Page<Comic> findComicsByTitleIsContainingIgnoreCase(String title, Pageable pageable);

    Page<Comic> findComicsByTitleIsStartingWithIgnoreCase(String title, Pageable pageable);

}