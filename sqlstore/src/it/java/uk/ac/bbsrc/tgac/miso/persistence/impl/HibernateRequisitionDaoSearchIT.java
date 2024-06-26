package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateRequisitionDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID,
      SearchType.IDS, SearchType.STATUS, SearchType.ENTERED, SearchType.UPDATED, SearchType.CREATOR,
      SearchType.MODIFIER);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias");

  public HibernateRequisitionDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS, "alias");
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateRequisitionDao sut = new HibernateRequisitionDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public List<String> getStatusTestValues() {
    return Arrays.asList("ongoing", "stopped", "paused");
  }

}
