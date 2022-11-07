package org.yomikaze.persistence.repository;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AccountRepositoryTest.class,
    GenreRepositoryTest.class,
    ComicRepositoryTest.class,
    ComicTest.class,
})
public class RepositoryTestSuite {
}
