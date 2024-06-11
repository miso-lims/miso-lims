package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateInstrumentModelDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES =
      EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias");

  public HibernateInstrumentModelDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS, "alias");
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateInstrumentModelDao sut = new HibernateInstrumentModelDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
