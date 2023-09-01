package uk.ac.bbsrc.tgac.miso;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;

public abstract class HibernatePaginatedDataSourceIT extends AbstractDAOTest {

  public enum SearchType {
    ARCHIVED, ARRAYED, BARCODE, BATCH, BOX, BOX_TYPE, BOX_USE, BULK_LOOKUP, CATEGORY, //
    CLASS, CREATED, CREATOR, DISTRIBUTED, DISTRIBUTION_RECIPIENT, DRAFT, //
    ENTERED, EXTERNAL_NAME, FULFILLED, FREEZER, GHOST, GROUP_ID, HEALTH, ID, //
    IDS, INDEX, INSTRUMENT_TYPE, KIT_NAME, KIT_TYPE, LAB, MODEL, MODIFIER, //
    PENDING, PLATFORM_TYPE, POOL, PROJECT, QUERY, RECEIVED, RECIPIENT_GROUPS, //
    REQUISITION, SEQUENCER, SEQUENCING_PARAMETERS_ID, //
    SEQUENCING_PARAMETERS_NAME, SOP_CATEGORY, STAGE, SUBPROJECT, TIMEPOINT, //
    TISSUE_ORIGIN, TISSUE_TYPE, TRANSFER_TYPE, UPDATED, WORKSET, WORKSTATION;
  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private PaginatedDataSource<?> sut;

  @Before
  public void setup() {
    sut = constructTestSubject();
  }

  private final Collection<SearchType> validSearchTypes;
  private final Collection<String> sortFields;
  private final String defaultSortProperty;

  public HibernatePaginatedDataSourceIT(Collection<SearchType> validSearchTypes, Collection<String> sortFields) {
    this(validSearchTypes, sortFields, "name");
  }

  public HibernatePaginatedDataSourceIT(Collection<SearchType> validSearchTypes, Collection<String> sortFields,
      String defaultSortProperty) {
    this.validSearchTypes = validSearchTypes;
    this.sortFields = sortFields;
    this.defaultSortProperty = defaultSortProperty;
  }

  protected abstract PaginatedDataSource<?> constructTestSubject();

  @Test
  public void testSearchByArchived() throws Exception {
    testSearch(PaginationFilter.archived(true), SearchType.ARCHIVED);
  }

  @Test
  public void testSearchByNotArchived() throws Exception {
    testSearch(PaginationFilter.archived(false), SearchType.ARCHIVED);
  }

  @Test
  public void testSearchByArrayed() throws Exception {
    testSearch(PaginationFilter.arrayed(true), SearchType.ARRAYED);
  }

  @Test
  public void testSearchByNotArrayed() throws Exception {
    testSearch(PaginationFilter.arrayed(false), SearchType.ARRAYED);
  }

  @Test
  public void testSearchByBarcode() throws Exception {
    testSearch(PaginationFilter.barcode("11111111"), SearchType.BARCODE);
  }

  @Test
  public void testSearchByBatch() throws Exception {
    testSearch(PaginationFilter.batchId("bad"), SearchType.BATCH);
  }

  @Test
  public void testSearchByBox() throws Exception {
    testSearch(PaginationFilter.box("BOX123"), SearchType.BOX);
  }

  @Test
  public void testSearchByBoxNone() throws Exception {
    testSearch(PaginationFilter.box(""), SearchType.BOX);
  }

  @Test
  public void testSearchByBoxType() throws Exception {
    testSearch(PaginationFilter.boxType(BoxType.STORAGE), SearchType.BOX_TYPE);
  }

  @Test
  public void testSearchByBoxUse() throws Exception {
    testSearch(PaginationFilter.boxUse(1L), SearchType.BOX_USE);
  }

  @Test
  public void testSearchByBulkLookup() throws Exception {
    testSearch(PaginationFilter.bulkLookup(Arrays.asList("identifier1", "identifier2")), SearchType.BULK_LOOKUP);
  }

  @Test
  public void testSearchByClass() throws Exception {
    testSearch(PaginationFilter.sampleClass("class"), SearchType.CLASS);
  }

  @Test
  public void testSearchByCreated() throws Exception {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-03-02"), LimsUtils.parseDate("2021-03-02"), DateType.CREATE),
        SearchType.CREATED);
  }

  @Test
  public void testSearchByEntered() throws Exception {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-03-02"), LimsUtils.parseDate("2021-03-02"), DateType.ENTERED),
        SearchType.ENTERED);
  }

  @Test
  public void testSearchByUpdated() throws Exception {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-03-02"), LimsUtils.parseDate("2021-03-02"), DateType.UPDATE),
        SearchType.UPDATED);
  }

  @Test
  public void testSearchByReceived() throws Exception {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-03-02"), LimsUtils.parseDate("2021-03-02"), DateType.RECEIVE),
        SearchType.RECEIVED);
  }

  @Test
  public void testSearchByDistributed() throws Exception {
    testSearch(
        PaginationFilter.date(LimsUtils.parseDate("2021-03-02"), LimsUtils.parseDate("2021-03-02"),
            DateType.DISTRIBUTED),
        SearchType.DISTRIBUTED);
  }

  @Test
  public void testSearchByDistributionRecipient() throws Exception {
    testSearch(PaginationFilter.distributedTo("someone"), SearchType.DISTRIBUTION_RECIPIENT);
  }

  @Test
  public void testSearchByNoDistributionRecipient() throws Exception {
    testSearch(PaginationFilter.distributedTo(""), SearchType.DISTRIBUTION_RECIPIENT);
  }

  @Test
  public void testSearchByExternalName() throws Exception {
    testSearch(PaginationFilter.external("external"), SearchType.EXTERNAL_NAME);
  }

  @Test
  public void testSearchByFulfilled() throws Exception {
    testSearch(PaginationFilter.fulfilled(true), SearchType.FULFILLED);
  }

  @Test
  public void testSearchByNotFulfilled() throws Exception {
    testSearch(PaginationFilter.fulfilled(false), SearchType.FULFILLED);
  }

  @Test
  public void testSearchByDraft() throws Exception {
    testSearch(PaginationFilter.draft(true), SearchType.DRAFT);
  }

  @Test
  public void testSearchByGroupId() throws Exception {
    testSearch(PaginationFilter.groupId("groupid"), SearchType.GROUP_ID);
  }

  @Test
  public void testSearchByGhost() throws Exception {
    testSearch(PaginationFilter.ghost(true), SearchType.GHOST);
  }

  @Test
  public void testSearchByHealth() throws Exception {
    testSearch(PaginationFilter.health(EnumSet.of(HealthType.Completed)), SearchType.HEALTH);
  }

  @Test
  public void testSearchById() throws Exception {
    testSearch(PaginationFilter.id(1L), SearchType.ID);
  }

  @Test
  public void testSearchByIds() throws Exception {
    testSearch(PaginationFilter.ids(Arrays.asList(1L, 2L, 3L)), SearchType.IDS);
  }

  @Test
  public void testSearchByIndex() throws Exception {
    testSearch(PaginationFilter.index("ACGT"), SearchType.INDEX);
  }

  @Test
  public void testSearchByIndexNone() throws Exception {
    testSearch(PaginationFilter.index(""), SearchType.INDEX);
  }

  @Test
  public void testSearchByLab() throws Exception {
    testSearch(PaginationFilter.lab("lab"), SearchType.LAB);
  }

  @Test
  public void testSearchByInstrumentType() throws Exception {
    testSearch(PaginationFilter.instrumentType(InstrumentType.SEQUENCER), SearchType.INSTRUMENT_TYPE);
  }

  @Test
  public void testSearchByKitType() throws Exception {
    testSearch(PaginationFilter.kitType(KitType.LIBRARY), SearchType.KIT_TYPE);
  }

  @Test
  public void testSearchByKitName() throws Exception {
    testSearch(PaginationFilter.kitName("kit"), SearchType.KIT_NAME);
  }

  @Test
  public void testSearchByPending() throws Exception {
    testSearch(PaginationFilter.pending(), SearchType.PENDING);
  }

  @Test
  public void testSearchByPlatformType() throws Exception {
    testSearch(PaginationFilter.platformType(PlatformType.ILLUMINA), SearchType.PLATFORM_TYPE);
  }

  @Test
  public void testSearchByPoolId() throws Exception {
    testSearch(PaginationFilter.pool(1L), SearchType.POOL);
  }

  @Test
  public void testSearchByProjectId() throws Exception {
    testSearch(PaginationFilter.project(1L), SearchType.PROJECT);
  }

  @Test
  public void testSearchByQuery() throws Exception {
    testSearch(PaginationFilter.query("test"), SearchType.QUERY);
  }

  @Test
  public void testSearchBySequencerId() throws Exception {
    testSearch(PaginationFilter.sequencer(1L), SearchType.SEQUENCER);
  }

  @Test
  public void testSearchBySequencingParametersId() throws Exception {
    testSearch(PaginationFilter.sequencingParameters(1L), SearchType.SEQUENCING_PARAMETERS_ID);
  }

  @Test
  public void testSearchBySequencingParmetersName() throws Exception {
    testSearch(PaginationFilter.sequencingParameters("params"), SearchType.SEQUENCING_PARAMETERS_NAME);
  }

  @Test
  public void testSearchBySequencingParmetersNameNone() throws Exception {
    testSearch(PaginationFilter.sequencingParameters(""), SearchType.SEQUENCING_PARAMETERS_NAME);
  }

  @Test
  public void testSearchBySubproject() throws Exception {
    testSearch(PaginationFilter.subproject("subproject"), SearchType.SUBPROJECT);
  }

  @Test
  public void testSearchByNoSubproject() throws Exception {
    testSearch(PaginationFilter.subproject(""), SearchType.SUBPROJECT);
  }

  @Test
  public void testSearchByCreator() throws Exception {
    testSearch(PaginationFilter.user("me", true), SearchType.CREATOR);
  }

  @Test
  public void testSearchByModifier() throws Exception {
    testSearch(PaginationFilter.user("me", false), SearchType.MODIFIER);
  }

  @Test
  public void testSearchByFreezer() throws Exception {
    testSearch(PaginationFilter.freezer("freezer"), SearchType.FREEZER);
  }

  @Test
  public void testSearchByRequisitionId() throws Exception {
    testSearch(PaginationFilter.requisitionId(1L), SearchType.REQUISITION);
  }

  @Test
  public void testSearchByRequisition() throws Exception {
    testSearch(PaginationFilter.requisition("requisition"), SearchType.REQUISITION);
  }

  @Test
  public void testSearchByRecipientGroups() throws Exception {
    Group group1 = new Group();
    group1.setId(1L);
    Group group2 = new Group();
    group2.setId(2L);
    testSearch(PaginationFilter.recipientGroups(Arrays.asList(group1, group2)), SearchType.RECIPIENT_GROUPS);
  }

  @Test
  public void testSearchByTransferType() throws Exception {
    testSearch(PaginationFilter.transferType(TransferType.RECEIPT), SearchType.TRANSFER_TYPE);
  }

  @Test
  public void testSearchByTimepoint() throws Exception {
    testSearch(PaginationFilter.timepoint("first"), SearchType.TIMEPOINT);
  }

  @Test
  public void testSearchByTimepointNone() throws Exception {
    testSearch(PaginationFilter.timepoint(""), SearchType.TIMEPOINT);
  }

  @Test
  public void testSearchByTissueOrigin() throws Exception {
    testSearch(PaginationFilter.tissueOrigin("Origin"), SearchType.TISSUE_ORIGIN);
  }

  @Test
  public void testSearchByTissueType() throws Exception {
    testSearch(PaginationFilter.tissueType("Type"), SearchType.TISSUE_TYPE);
  }

  @Test
  public void testSearchByCategory() throws Exception {
    testSearch(PaginationFilter.category("Category"), SearchType.CATEGORY);
  }

  @Test
  public void testSearchByStage() throws Exception {
    testSearch(PaginationFilter.stage("Stage"), SearchType.STAGE);
  }

  @Test
  public void testSearchBySopCategory() throws Exception {
    testSearch(PaginationFilter.category(SopCategory.LIBRARY), SearchType.SOP_CATEGORY);
  }

  @Test
  public void testSearchByWorksetId() throws Exception {
    testSearch(PaginationFilter.workset(1L), SearchType.WORKSET);
  }

  @Test
  public void testSearchByModel() throws Exception {
    testSearch(PaginationFilter.model("model"), SearchType.MODEL);
  }

  @Test
  public void testSearchByWorkstation() throws Exception {
    testSearch(PaginationFilter.workstation("station"), SearchType.WORKSTATION);
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter, SearchType searchType) throws Exception {
    // verify Hibernate mappings by ensuring that no exception is thrown unless expected
    if (!validSearchTypes.contains(searchType)) {
      exception.expect(RuntimeException.class);
    }
    assertNotNull(sut.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, defaultSortProperty, filter));
  }

  @Test
  public void testSorts() throws Exception {
    for (String sort : sortFields) {
      assertNotNull(sut.list(0, 0, true, sort));
    }
  }

}
