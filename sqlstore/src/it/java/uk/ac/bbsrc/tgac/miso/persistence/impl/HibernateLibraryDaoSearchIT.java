package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateLibraryDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES =
      EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS, SearchType.REQUISITION,
          SearchType.FREEZER, SearchType.DESIGN, SearchType.DISTRIBUTED, SearchType.DISTRIBUTION_RECIPIENT,
          SearchType.TISSUE_ORIGIN, SearchType.TISSUE_TYPE, SearchType.PROJECT, SearchType.BOX, SearchType.CREATED,
          SearchType.UPDATED, SearchType.RECEIVED, SearchType.WORKSET, SearchType.CREATOR, SearchType.MODIFIER,
          SearchType.BULK_LOOKUP, SearchType.ENTERED, SearchType.GROUP_ID, SearchType.INDEX, SearchType.KIT_NAME,
          SearchType.PLATFORM_TYPE, SearchType.WORKSTATION, SearchType.BARCODE);

  private static final List<String> SORT_FIELDS = Arrays.asList("effectiveTissueOriginAlias",
      "effectiveTissueTypeAlias", "parentSampleId", "parentSampleAlias", "projectCode");

  public HibernateLibraryDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateLibraryDao sut = new HibernateLibraryDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

}
