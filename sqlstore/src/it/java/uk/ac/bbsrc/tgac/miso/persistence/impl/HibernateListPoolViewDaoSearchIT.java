package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateListPoolViewDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES =
      EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS,
          SearchType.INDEX, SearchType.BOX, SearchType.PROJECT, SearchType.PLATFORM_TYPE, SearchType.FREEZER,
          SearchType.DISTRIBUTION_RECIPIENT, SearchType.CREATED, SearchType.ENTERED, SearchType.UPDATED,
          SearchType.CREATOR, SearchType.MODIFIER, SearchType.DISTRIBUTED, SearchType.RECEIVED, SearchType.BARCODE);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias", "creationDate");

  public HibernateListPoolViewDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateListPoolViewDao sut = new HibernateListPoolViewDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

}
