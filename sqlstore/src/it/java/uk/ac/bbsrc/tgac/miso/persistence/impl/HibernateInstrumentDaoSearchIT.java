package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.HibernatePaginatedDataSourceIT;

public class HibernateInstrumentDaoSearchIT extends HibernatePaginatedDataSourceIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS,
      SearchType.PLATFORM_TYPE, SearchType.ARCHIVED, SearchType.INSTRUMENT_TYPE, SearchType.CREATED, SearchType.MODEL,
      SearchType.WORKSTATION);
  private static final List<String> SORT_FIELDS = Arrays.asList("name", "platformType", "instrumentModelAlias");

  public HibernateInstrumentDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected HibernatePaginatedDataSource<?> constructTestSubject() {
    HibernateInstrumentDao sut = new HibernateInstrumentDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
