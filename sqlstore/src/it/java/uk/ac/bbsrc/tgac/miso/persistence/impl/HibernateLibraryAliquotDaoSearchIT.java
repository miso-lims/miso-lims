package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;

public class HibernateLibraryAliquotDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID,
      SearchType.IDS, SearchType.PROJECT, SearchType.POOL, SearchType.PLATFORM_TYPE, SearchType.INDEX,
      SearchType.GROUP_ID, SearchType.DISTRIBUTION_RECIPIENT, SearchType.CREATED, SearchType.ENTERED,
      SearchType.UPDATED, SearchType.CREATOR, SearchType.MODIFIER, SearchType.BOX, SearchType.FREEZER,
      SearchType.DISTRIBUTED, SearchType.RECEIVED, SearchType.BARCODE);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias");

  public HibernateLibraryAliquotDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS, "alias");
  }

  @Override
  protected HibernatePaginatedDataSource<?> constructTestSubject() {
    HibernateLibraryAliquotDao sut = new HibernateLibraryAliquotDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
