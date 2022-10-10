package io.gitlab.zeromatter.yomikaze.persistence.repository;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Comic;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Genre;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ComicRepository extends CrudRepository<Comic, Snowflake> {

    @Query(
            "select c from #{#entityName} c " +
                    "join c.genres g " +
                    "where g in (:#{#genres}) " +
                    "group by c.id " +
                    "having count(c.id) = :#{#genres.size().longValue()}"
    )
    Set<Comic> findByGenresContainsAll(@Param("genres") Set<Genre> genres);

    @Query(
            "select c from #{#entityName} c " +
                    "join c.genres g " +
                    "where g not in (:#{#genres})"
    )
    Set<Comic> findByGenresContainsNone(@Param("genres") Set<Genre> genres);

    default Set<Comic> findByGenresContainsAllAndNone(Set<Genre> whitelist, Set<Genre> blacklist) {
        Set<Comic> all = findByGenresContainsAll(whitelist);
        Set<Comic> none = findByGenresContainsAll(blacklist);
        all.removeAll(none);
        return all;
    }

}