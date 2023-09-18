package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateKitDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES =
      EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS,
          SearchType.KIT_TYPE, SearchType.KIT_NAME, SearchType.CREATOR, SearchType.MODIFIER);

  private static final List<String> SORT_FIELDS = Arrays.asList("name");

  public HibernateKitDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateKitDao sut = new HibernateKitDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
