package org.yomikaze.persistence.repository;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AccountRepositoryTest.class, ComicRepositoryTest.class, GenreRepositoryTest.class})
public class RepositoryTestSuite {
}
