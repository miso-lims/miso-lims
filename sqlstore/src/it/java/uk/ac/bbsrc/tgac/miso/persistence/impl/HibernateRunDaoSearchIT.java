package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.HibernatePaginatedDataSourceIT;

public class HibernateRunDaoSearchIT extends HibernatePaginatedDataSourceIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS,
      SearchType.PROJECT, SearchType.SEQUENCING_PARAMETERS_NAME, SearchType.HEALTH, SearchType.PLATFORM_TYPE, SearchType.INDEX,
      SearchType.SEQUENCER, SearchType.CREATED, SearchType.ENTERED, SearchType.UPDATED, SearchType.CREATOR, SearchType.MODIFIER);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias", "platformType", "status", "endDate");

  public HibernateRunDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected HibernatePaginatedDataSource<?> constructTestSubject() {
    HibernateRunDao sut = new HibernateRunDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
