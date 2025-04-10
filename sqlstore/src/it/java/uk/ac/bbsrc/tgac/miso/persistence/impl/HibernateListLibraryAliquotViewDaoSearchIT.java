package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.PaginationFilterSinkIT;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public class HibernateListLibraryAliquotViewDaoSearchIT extends PaginationFilterSinkIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES =
      EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS, SearchType.DESIGN,
          SearchType.FREEZER, SearchType.DISTRIBUTED, SearchType.DISTRIBUTION_RECIPIENT, SearchType.TISSUE_ORIGIN,
          SearchType.TISSUE_TYPE, SearchType.PROJECT, SearchType.PLATFORM_TYPE, SearchType.BOX, SearchType.INDEX,
          SearchType.POOL, SearchType.ENTERED, SearchType.CREATED, SearchType.UPDATED, SearchType.RECEIVED,
          SearchType.WORKSET, SearchType.CREATOR, SearchType.MODIFIER, SearchType.BULK_LOOKUP, SearchType.BARCODE);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias", "lastModified", "library.parentSampleId",
      "library.parentSampleAlias", "libraryPlatformType", "library.platformType", "creatorName", "creationDate",
      "effectiveTissueOriginAlias", "effectiveTissueTypeAlias", "projectCode");

  public HibernateListLibraryAliquotViewDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected PaginatedDataSource<?> constructTestSubject() {
    HibernateListLibraryAliquotViewDao sut = new HibernateListLibraryAliquotViewDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

}
