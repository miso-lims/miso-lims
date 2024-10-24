package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateLibraryAliquotDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID,
      SearchType.IDS, SearchType.PROJECT, SearchType.POOL, SearchType.PLATFORM_TYPE, SearchType.INDEX,
      SearchType.GROUP_ID, SearchType.DISTRIBUTION_RECIPIENT, SearchType.CREATED, SearchType.ENTERED,
      SearchType.UPDATED, SearchType.CREATOR, SearchType.MODIFIER, SearchType.BOX, SearchType.FREEZER,
      SearchType.DISTRIBUTED, SearchType.RECEIVED, SearchType.BARCODE, SearchType.DESIGN);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias");

  public HibernateLibraryAliquotDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS, "alias");
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateLibraryAliquotDao sut = new HibernateLibraryAliquotDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

}
