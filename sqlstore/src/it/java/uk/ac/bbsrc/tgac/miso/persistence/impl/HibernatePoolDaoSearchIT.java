package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernatePoolDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID,
      SearchType.IDS, SearchType.PROJECT, SearchType.PLATFORM_TYPE, SearchType.INDEX, SearchType.DISTRIBUTION_RECIPIENT,
      SearchType.CREATED, SearchType.UPDATED, SearchType.ENTERED, SearchType.CREATOR, SearchType.MODIFIER,
      SearchType.BOX, SearchType.FREEZER, SearchType.RECEIVED, SearchType.DISTRIBUTED, SearchType.BULK_LOOKUP,
      SearchType.BARCODE);

  private static final List<String> SORT_FIELDS = Arrays.asList("alias", "id");

  public HibernatePoolDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernatePoolDao sut = new HibernatePoolDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

}
