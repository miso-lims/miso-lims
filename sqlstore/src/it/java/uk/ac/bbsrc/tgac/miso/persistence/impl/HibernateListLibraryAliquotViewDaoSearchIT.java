package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.HibernatePaginatedDataSourceIT;

public class HibernateListLibraryAliquotViewDaoSearchIT extends HibernatePaginatedDataSourceIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS,
      SearchType.FREEZER, SearchType.DISTRIBUTED, SearchType.DISTRIBUTION_RECIPIENT, SearchType.TISSUE_ORIGIN, SearchType.TISSUE_TYPE,
      SearchType.PROJECT, SearchType.PLATFORM_TYPE, SearchType.BOX, SearchType.INDEX, SearchType.POOL, SearchType.CREATED,
      SearchType.UPDATED, SearchType.RECEIVED, SearchType.WORKSET, SearchType.CREATOR, SearchType.MODIFIER, SearchType.BULK_LOOKUP,
      SearchType.BARCODE);
  private static final List<String> SORT_FIELDS = Arrays.asList("alias", "lastModified", "library.parentSampleId",
      "library.parentSampleAlias", "libraryPlatformType", "library.platformType", "creatorName", "creationDate",
      "effectiveTissueOriginLabel", "effectiveTissueTypeLabel");

  public HibernateListLibraryAliquotViewDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected HibernatePaginatedDataSource<?> constructTestSubject() {
    HibernateListLibraryAliquotViewDao sut = new HibernateListLibraryAliquotViewDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

}
