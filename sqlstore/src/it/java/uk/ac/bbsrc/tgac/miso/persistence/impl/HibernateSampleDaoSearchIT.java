package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.HibernatePaginatedDataSourceIT;

public class HibernateSampleDaoSearchIT extends HibernatePaginatedDataSourceIT {

  private static final EnumSet<SearchType> VALID_SEARCH_TYPES = EnumSet.of(SearchType.QUERY, SearchType.ID, SearchType.IDS,
      SearchType.FREEZER, SearchType.DISTRIBUTED, SearchType.DISTRIBUTION_RECIPIENT, SearchType.TISSUE_ORIGIN, SearchType.TISSUE_TYPE,
      SearchType.PROJECT, SearchType.BOX, SearchType.CREATED, SearchType.UPDATED, SearchType.RECEIVED, SearchType.WORKSET,
      SearchType.CREATOR, SearchType.MODIFIER, SearchType.BULK_LOOKUP, SearchType.CLASS, SearchType.LAB, SearchType.EXTERNAL_NAME,
      SearchType.SUBPROJECT, SearchType.ENTERED, SearchType.GHOST, SearchType.REQUISITION, SearchType.TIMEPOINT, SearchType.ARRAYED,
      SearchType.GROUP_ID);

  private static final List<String> SORT_FIELDS = Arrays.asList("effectiveTissueOriginLabel", "effectiveTissueTypeLabel", "sampleClassId");

  public HibernateSampleDaoSearchIT() {
    super(VALID_SEARCH_TYPES, SORT_FIELDS);
  }

  @Override
  protected HibernatePaginatedDataSource<?> constructTestSubject() {
    HibernateSampleDao sut = new HibernateSampleDao();
    sut.setSessionFactory(getSessionFactory());
    sut.setDetailedSample(true);
    return sut;
  }

}
