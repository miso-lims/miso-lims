package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateProjectDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID,
      SearchType.IDS, SearchType.CREATED, SearchType.UPDATED, SearchType.CREATOR, SearchType.MODIFIER,
      SearchType.ENTERED, SearchType.STATUS, SearchType.PIPELINE, SearchType.REB_EXPIRY, SearchType.REB_NUMBER);

  private static final List<String> SORT_FIELDS = Arrays.asList("name", "title", "code", "description", "status");

  public HibernateProjectDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateProjectDao sut = new HibernateProjectDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
