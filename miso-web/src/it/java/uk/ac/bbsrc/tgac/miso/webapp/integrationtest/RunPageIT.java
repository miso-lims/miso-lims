package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage.LaneQC;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage.PoolSearch;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage.RunTableWrapperId;

public class RunPageIT extends AbstractIT {

  @Before
  public void setup() {
    login();
  }

  @Test
  public void testCreatePacBio() throws Exception {
    RunPage page1 = RunPage.getForCreate(getDriver(), getBaseUrl(), 5001L);

    // default values
    Map<RunPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, null);
    fields.put(Field.NAME, null);
    fields.put(Field.ALIAS, null);
    fields.put(Field.PLATFORM, "PacBio");
    fields.put(Field.SEQUENCER, "PacBio_SR_5001 - PacBio RS II");
    fields.put(Field.DESCRIPTION, null);
    fields.put(Field.FILE_PATH, null);
    fields.put(Field.STATUS, "Unknown");
    fields.put(Field.START_DATE, null);
    fields.put(Field.COMPLETION_DATE, null);
    assertFieldValues("default values", fields, page1);

    // enter run info
    Map<RunPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.ALIAS, "Test_PacBio_Run_Creation");
    changes.put(Field.DESCRIPTION, "test run creation");
    changes.put(Field.FILE_PATH, "/nowhere");
    changes.put(Field.STATUS, "Running");
    changes.put(Field.START_DATE, "2017-09-01");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("pre-save", fields, page1);

    RunPage page2 = page1.save();
    fields.remove(Field.ID);
    fields.remove(Field.NAME);
    assertFieldValues("post-save", fields, page2);
    long savedId = Long.parseLong(page2.getField(Field.ID));
    Run savedRun = (Run) getSession().get(PacBioRun.class, savedId);
    fields.put(Field.ID, Long.toString(savedId));
    fields.put(Field.NAME, "RUN" + savedId);
    assertRunAttributes(fields, savedRun);
  }

  @Test
  public void testCreateIllumina() throws Exception {
    RunPage page1 = RunPage.getForCreate(getDriver(), getBaseUrl(), 5002L);

    // default values
    Map<RunPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, null);
    fields.put(Field.NAME, null);
    fields.put(Field.ALIAS, null);
    fields.put(Field.PLATFORM, "Illumina");
    fields.put(Field.SEQUENCER, "HiSeq_SR_5002 - Illumina HiSeq 2500");
    fields.put(Field.SEQ_PARAMS, "SELECT");
    fields.put(Field.DESCRIPTION, null);
    fields.put(Field.FILE_PATH, null);
    fields.put(Field.STATUS, "Unknown");
    fields.put(Field.START_DATE, null);
    fields.put(Field.COMPLETION_DATE, null);
    assertFieldValues("default values", fields, page1);

    // enter run info
    Map<RunPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.ALIAS, "Test_Illumina_Run_Creation");
    changes.put(Field.SEQ_PARAMS, "Rapid Run 2x151");
    changes.put(Field.DESCRIPTION, "test Illumina run creation");
    changes.put(Field.FILE_PATH, "/nowhere");
    changes.put(Field.STATUS, "Started");
    changes.put(Field.START_DATE, "2017-09-01");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("pre-save", fields, page1);

    RunPage page2 = page1.save();
    fields.remove(Field.ID);
    fields.remove(Field.NAME);
    assertFieldValues("post-save", fields, page2);
    long savedId = Long.parseLong(page2.getField(Field.ID));
    Run savedRun = (Run) getSession().get(IlluminaRun.class, savedId);
    fields.put(Field.ID, Long.toString(savedId));
    fields.put(Field.NAME, "RUN" + savedId);
    assertRunAttributes(fields, savedRun);
  }

  @Test
  public void testChangeValues() throws Exception {
    // goal: change all changeable values
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5001);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "5001");
    fields.put(Field.NAME, "RUN5001");
    fields.put(Field.ALIAS, "Change_Values_Run");
    fields.put(Field.PLATFORM, "Illumina");
    fields.put(Field.SEQUENCER, "HiSeq_SR_5002 - Illumina HiSeq 2500");
    fields.put(Field.SEQ_PARAMS, "Rapid Run 2x151");
    fields.put(Field.DESCRIPTION, "description");
    fields.put(Field.FILE_PATH, "/filePath");
    fields.put(Field.NUM_CYCLES, "75");
    fields.put(Field.CALL_CYCLE, "35");
    fields.put(Field.IMG_CYCLE, "34");
    fields.put(Field.SCORE_CYCLE, "33");
    fields.put(Field.PAIRED_END, "true");
    fields.put(Field.STATUS, "Running");
    fields.put(Field.START_DATE, "2017-09-05");
    fields.put(Field.COMPLETION_DATE, null);
    assertFieldValues("loaded", fields, page);

    // make changes
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.ALIAS, "Changed_Alias_Run");
    changes.put(Field.SEQ_PARAMS, "1x151");
    changes.put(Field.DESCRIPTION, "changed description");
    changes.put(Field.FILE_PATH, "/new/filePath");
    changes.put(Field.NUM_CYCLES, "100");
    changes.put(Field.CALL_CYCLE, "99");
    changes.put(Field.IMG_CYCLE, "80");
    changes.put(Field.SCORE_CYCLE, "183");
    changes.put(Field.PAIRED_END, "false");
    changes.put(Field.STATUS, "Failed");
    changes.put(Field.COMPLETION_DATE, "2017-09-10");
    page.setFields(changes);

    // copy unchanged
    fields.forEach((key, val) -> {
      if (!changes.containsKey(key)) {
        changes.put(key, val);
      }
    });
    assertFieldValues("changes pre-save", changes, page);

    RunPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("changes post-save", changes, page2);

    Run run = (Run) getSession().get(Run.class, 5001L);
    assertRunAttributes(changes, run);
  }

  @Test
  public void testAddExistingContainer() throws Exception {
    // goal: add an existing container to a run with no containers
    Run run = (Run) getSession().get(Run.class, 5002L);
    assertTrue(run.getSequencerPartitionContainers().isEmpty());

    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5002L);
    RunPage page2 = page.addContainer("EXISTING", "Illumina", false);
    assertTrue(page2.getTable(RunTableWrapperId.CONTAINER).doesColumnContain(Columns.SERIAL_NUMBER, "EXISTING"));

    Run addedRun = (Run) getSession().get(Run.class, 5002L);
    assertEquals(1, addedRun.getSequencerPartitionContainers().size());
    assertEquals("EXISTING", addedRun.getSequencerPartitionContainers().get(0).getIdentificationBarcode());
  }

  @Test
  public void testRemoveContainer() throws Exception {
    // goal: remove a container from a run with one container
    Run run = (Run) getSession().get(Run.class, 5003L);
    assertEquals(1, run.getSequencerPartitionContainers().size());
    assertEquals("REMOVABLE", run.getSequencerPartitionContainers().get(0).getIdentificationBarcode());

    RunPage page1 = RunPage.getForEdit(getDriver(), getBaseUrl(), 5003L);
    RunPage page2 = page1.removeContainer(0);
    assertFalse(page2.getTable(RunTableWrapperId.CONTAINER).doesColumnContain(Columns.SERIAL_NUMBER, "REMOVABLE"));

    Run strippedRun = (Run) getSession().get(Run.class, 5003L);
    assertTrue(strippedRun.getSequencerPartitionContainers().isEmpty());
  }

  @Test
  public void testAssignPoolToTwoLanes() throws Exception {
    // goal: assign a pool to two empty lanes of a run
    final String poolAlias = "RUN_POOL_ADD";
    Run initial = (Run) getSession().get(Run.class, 5004L);
    assertEquals(1, initial.getSequencerPartitionContainers().size());
    initial.getSequencerPartitionContainers().get(0).getPartitions()
        .forEach(partition -> assertNull(partition.getPool()));

    RunPage page1 = RunPage.getForEdit(getDriver(), getBaseUrl(), 5004L);
    List<String> page1Pools = page1.getTable(RunTableWrapperId.PARTITION).getColumnValues(Columns.POOL);
    assertEquals(0, page1Pools.stream().filter(val -> val.contains(poolAlias)).collect(Collectors.toList()).size());

    BigDecimal concentration = new BigDecimal("12.34");
    RunPage page2 = page1.assignPools(Arrays.asList(0, 1), PoolSearch.SEARCH, poolAlias, concentration,
        ConcentrationUnit.NANOMOLAR);
    List<String> columnValues = page2.getTable(RunTableWrapperId.PARTITION).getColumnValues(Columns.POOL);
    assertEquals(2, columnValues.stream().filter(val -> val.contains(poolAlias)).collect(Collectors.toList()).size());

    Run run = (Run) getSession().get(Run.class, 5004L);
    assertEquals(1, run.getSequencerPartitionContainers().size());
    int partitionsSet = 0;
    for (Partition partition : run.getSequencerPartitionContainers().get(0).getPartitions()) {
      if (partition.getPartitionNumber() > 2) {
        assertNull(partition.getPool());
      } else {
        assertNotNull(partition.getPool());
        assertEquals(LimsUtils.toNiceString(concentration),
            LimsUtils.toNiceString(partition.getLoadingConcentration()));
        assertEquals(ConcentrationUnit.NANOMOLAR, partition.getLoadingConcentrationUnits());
        partitionsSet++;
      }
    }
    assertEquals(2, partitionsSet);
  }

  @Test
  public void testRemovePoolsFromLanes() throws Exception {
    // goal: remove pools from two lane of a run
    final String poolAlias = "RUN_POOL_REMOVE";
    Run initial = (Run) getSession().get(Run.class, 5005L);
    assertEquals(1, initial.getSequencerPartitionContainers().size());
    for (Partition partition : initial.getSequencerPartitionContainers().get(0).getPartitions()) {
      if (partition.getPartitionNumber() > 2) {
        assertNull(partition.getPool());
      } else {
        assertNotNull(partition.getPool());
      }
    }

    RunPage page1 = RunPage.getForEdit(getDriver(), getBaseUrl(), 5005L);
    List<String> page1Pools = page1.getTable(RunTableWrapperId.PARTITION).getColumnValues(Columns.POOL);
    assertEquals(2, page1Pools.stream().filter(val -> val.contains(poolAlias)).collect(Collectors.toList()).size());
    RunPage page2 = page1.assignPools(Arrays.asList(0, 1), PoolSearch.NO_POOL, null);
    List<String> page2Pools = page2.getTable(RunTableWrapperId.PARTITION).getColumnValues(Columns.POOL);
    assertEquals(0, page2Pools.stream().filter(val -> val.contains(poolAlias)).collect(Collectors.toList()).size());

    Run run = (Run) getSession().get(Run.class, 5005L);
    assertEquals(1, run.getSequencerPartitionContainers().size());
    run.getSequencerPartitionContainers().get(0).getPartitions()
        .forEach(partition -> assertNull(partition.getPool()));
  }

  @Test
  public void testReplacePoolInLane() throws Exception {
    // goal: assign a pool to one full lane of a run
    final String firstPool = "IPO5006";
    final String secondPool = "IPO5007";
    Run initial = (Run) getSession().get(Run.class, 5006L);
    assertEquals(1, initial.getSequencerPartitionContainers().size());
    Partition initialPartition = initial.getSequencerPartitionContainers().get(0).getPartitionAt(1);
    assertNotNull(initialPartition.getPool());
    assertEquals(firstPool, initialPartition.getPool().getName());

    RunPage page1 = RunPage.getForEdit(getDriver(), getBaseUrl(), 5006L);
    List<String> page1Pools = page1.getTable(RunTableWrapperId.PARTITION).getColumnValues(Columns.POOL);
    assertEquals(1, page1Pools.stream().filter(val -> val.contains(firstPool)).collect(Collectors.toList()).size());

    BigDecimal concentration = new BigDecimal("11.22");
    RunPage page2 = page1.assignPools(Arrays.asList(0), PoolSearch.SEARCH, secondPool, concentration,
        ConcentrationUnit.NANOGRAMS_PER_MICROLITRE);
    List<String> page2Pools = page2.getTable(RunTableWrapperId.PARTITION).getColumnValues(Columns.POOL);
    assertEquals(1, page2Pools.stream().filter(val -> val.contains(secondPool)).collect(Collectors.toList()).size());

    Run run = (Run) getSession().get(Run.class, 5006L);
    assertEquals(1, run.getSequencerPartitionContainers().size());
    Partition partition = run.getSequencerPartitionContainers().get(0).getPartitionAt(1);
    assertNotNull(partition.getPool());
    assertEquals(secondPool, partition.getPool().getName());
    assertEquals(LimsUtils.toNiceString(concentration), LimsUtils.toNiceString(partition.getLoadingConcentration()));
    assertEquals(ConcentrationUnit.NANOGRAMS_PER_MICROLITRE, partition.getLoadingConcentrationUnits());
  }

  @Test
  public void testSetLaneQcToFailedNoNote() throws Exception {
    // goal: set one lane QC to failed due to instrument issues
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5008L);
    assertEquals("(Unset)", page.getLaneInfo(Columns.QC_STATUS, 0));
    assertTrue(isStringEmptyOrNull(page.getLaneInfo(Columns.QC_NOTE, 0)));

    RunPage page2 = page.setPartitionQC(Arrays.asList(0), LaneQC.FAIL_INSTRUMENT, null);
    assertEquals(LaneQC.FAIL_INSTRUMENT, page2.getLaneInfo(Columns.QC_STATUS, 0));
    assertTrue(isStringEmptyOrNull(page.getLaneInfo(Columns.QC_NOTE, 0)));
  }

  @Test
  public void testSetLaneQcToFailedWithNote() throws Exception {
    // goal: set one lane QC to failed due to "other" issues
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5009L);
    assertEquals("(Unset)", page.getLaneInfo(Columns.QC_STATUS, 0));
    assertTrue(isStringEmptyOrNull(page.getLaneInfo(Columns.QC_NOTE, 0)));

    RunPage page2 = page.setPartitionQC(Arrays.asList(0), LaneQC.FAIL_OTHER, "Sequencer ran out of Cs");
    assertEquals(LaneQC.FAIL_OTHER, page2.getLaneInfo(Columns.QC_STATUS, 0));
    assertEquals("Sequencer ran out of Cs", page.getLaneInfo(Columns.QC_NOTE, 0));
  }

  @Test
  public void testChangeLaneQcFailedToOk() throws Exception {
    // goal: set one lane QC to failed, then change it to ok'd
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5010L);
    assertEquals("(Unset)", page.getLaneInfo(Columns.QC_STATUS, 0));
    assertTrue(isStringEmptyOrNull(page.getLaneInfo(Columns.QC_NOTE, 0)));

    RunPage page2 = page.setPartitionQC(Arrays.asList(0), LaneQC.FAIL_INSTRUMENT, null);
    assertEquals(LaneQC.FAIL_INSTRUMENT, page2.getLaneInfo(Columns.QC_STATUS, 0));
    assertTrue(isStringEmptyOrNull(page2.getLaneInfo(Columns.QC_NOTE, 0)));

    RunPage page3 = page2.setPartitionQC(Arrays.asList(0), LaneQC.OK_COLLAB, null);
    assertEquals(LaneQC.OK_COLLAB, page3.getLaneInfo(Columns.QC_STATUS, 0));
    assertTrue(isStringEmptyOrNull(page3.getLaneInfo(Columns.QC_NOTE, 0)));
  }

  @Test
  public void testPoolSearchByName() throws Exception {
    // goal: test pool search by name
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    String nameSearch = "IPO510";

    // search by partial name
    page.searchForPools(false, Arrays.asList(0), PoolSearch.SEARCH, nameSearch + "*");
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertFalse(poolIds.isEmpty());
    poolIds.forEach(poolId -> {
      Pool pool = (Pool) getSession().get(PoolImpl.class, poolId);
      assertTrue(pool.getName().startsWith(nameSearch));
    });
    // assert that pool with name "IPO200002" is not returned
    assertFalse(poolIds.contains(200002L));
  }

  @Test
  public void testPoolSearchByAlias() throws Exception {
    // goal: test pool search by alias
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    String aliasSearch = "POOL_SEARCH_1";

    // search by exact alias
    page.searchForPools(false, Arrays.asList(0), PoolSearch.SEARCH, aliasSearch);
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertEquals(1, poolIds.size());
    poolIds.forEach(poolId -> {
      Pool pool = (Pool) getSession().get(PoolImpl.class, poolId);
      assertTrue(aliasSearch.equals(pool.getAlias()));
    });
    // assert that pool with alias "POOL_SEARCH_2" is not returned
    assertFalse(poolIds.contains(5102L));
  }

  @Test
  public void testPoolSearchByBarcode() throws Exception {
    // goal: test pool search by identificationBarcode
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    String barcodeSearch = "ipobar";

    // search by partial identificationBarcode
    page.searchForPools(false, Arrays.asList(0), PoolSearch.SEARCH, barcodeSearch + "*");
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertTrue(poolIds.size() > 1);
    poolIds.forEach(poolId -> {
      Pool pool = (Pool) getSession().get(PoolImpl.class, poolId);
      assertTrue(pool.getIdentificationBarcode().startsWith(barcodeSearch));
    });
    // assert that pool with barcode "TIB_POOL" is not returned
    assertFalse(poolIds.contains(501L));
  }

  @Test
  public void testPoolSearchByDescription() throws Exception {
    // goal: test pool search by description
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    String descSearch = "swimming";

    // search by exact description
    page.searchForPools(false, Arrays.asList(0), PoolSearch.SEARCH, descSearch);
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertEquals(1, poolIds.size());
    poolIds.forEach(poolId -> {
      Pool pool = (Pool) getSession().get(PoolImpl.class, poolId);
      assertTrue(descSearch.equals(pool.getDescription()));
    });
    // assert that pool with description "cats" is not returned
    assertFalse(poolIds.contains(5102L));
  }

  @Test
  public void testPoolSearchOutstandingMatchedOrders() throws Exception {
    // goal: test pool search by outstanding orders with matched chemistry
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    // search by exact description
    page.searchForPools(false, Arrays.asList(0), PoolSearch.OUTSTANDING_MATCH, null);
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertEquals(1, poolIds.size());
    assertTrue(poolIds.contains(5103L));
    // assert that pool with different sequencing parameters is not returned
    assertFalse(poolIds.contains(5102L));
  }

  @Test
  public void testPoolSearchAllOutstandingOrders() throws Exception {
    // goal: pool search should return all outstanding orders
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    // search by exact description
    page.searchForPools(false, Arrays.asList(0), PoolSearch.OUTSTANDING_ALL, null);
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertTrue(poolIds.size() > 1);
    // assert that pool with fulfilled orders is not returned
    assertFalse(poolIds.contains(5104L));
  }

  @Ignore
  @Test
  public void testPoolSearchRecentlyModified() throws Exception {
    // goal: pool search should return pools in order of last modified descending
    // NB: since the PoolPickerResponse groups orders by pool, the pool sort order is
    // not necessarily preserved.
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    page.searchForPools(false, Arrays.asList(0), PoolSearch.RECENT, null);
    List<Long> poolIds = page.getPoolIdsFromTiles();
    assertTrue(poolIds.size() > 1);
    for (int i = 0; i < poolIds.size(); i++) {
      if (i + 1 < poolIds.size()) {
        Pool recent = (Pool) getSession().get(PoolImpl.class, poolIds.get(i));
        Pool older = (Pool) getSession().get(PoolImpl.class, poolIds.get(i + 1));
        // doing the assertion this way because copy-pasting means we have some equivalent dates
        assertFalse("recent: " + recent.getLastModified() + "; older: " + older.getLastModified(),
            recent.getLastModified().before(older.getLastModified()));
      }
    }
  }

  @Test
  public void testPoolTileWarnings() throws Exception {
    testPoolTileWarning("IPO801", "MISSING INDEX");
    testPoolTileWarning("IPO802", "Near-Duplicate Indices");
    testPoolTileWarning("IPO803", "DUPLICATE INDICES");
    testPoolTileWarning("IPO804", "Low Quality Libraries");
  }

  private void testPoolTileWarning(String search, String warning) throws Exception {
    RunPage page = RunPage.getForEdit(getDriver(), getBaseUrl(), 5100L);

    page.searchForPools(false, Arrays.asList(0), PoolSearch.SEARCH, search);
    List<String> poolWarnings = page.getPoolWarningsFromTiles();
    assertTrue(poolWarnings.size() >= 1);

    boolean containsWarning = false;
    for (String poolWarning : poolWarnings) {
      if (poolWarning.contains(warning)) {
        containsWarning = true;
        break;
      }
    }
    assertTrue(containsWarning);
  }

  private void assertRunAttributes(Map<RunPage.Field, String> expectedValues, Run run) {
    assertAttribute(Field.ID, expectedValues, Long.toString(run.getId()));
    assertAttribute(Field.NAME, expectedValues, run.getName());
    assertAttribute(Field.ALIAS, expectedValues, run.getAlias());
    assertAttribute(Field.PLATFORM, expectedValues, run.getPlatformType().getKey());
    assertAttribute(Field.SEQUENCER, expectedValues,
        run.getSequencer().getName() + " - " + run.getSequencer().getInstrumentModel().getAlias());
    assertAttribute(Field.SEQ_PARAMS, expectedValues,
        nullOrGet(run.getSequencingParameters(), SequencingParameters::getName));
    assertAttribute(Field.DESCRIPTION, expectedValues, nullOrToString(run.getDescription()));
    assertAttribute(Field.FILE_PATH, expectedValues, run.getFilePath());
    assertAttribute(Field.STATUS, expectedValues, run.getHealth().getKey());
    assertAttribute(Field.START_DATE, expectedValues, formatDate(run.getStartDate()));
    assertAttribute(Field.COMPLETION_DATE, expectedValues, formatDate(run.getCompletionDate()));
    if (run instanceof IlluminaRun) {
      assertIlluminaRunAttributes(expectedValues, (IlluminaRun) run);
    }
  }

  private void assertIlluminaRunAttributes(Map<RunPage.Field, String> expectedValues, IlluminaRun run) {
    assertAttribute(Field.NUM_CYCLES, expectedValues, nullOrToString(run.getNumCycles()));
    assertAttribute(Field.CALL_CYCLE, expectedValues, nullOrToString(run.getCallCycle()));
    assertAttribute(Field.IMG_CYCLE, expectedValues, nullOrToString(run.getImgCycle()));
    assertAttribute(Field.SCORE_CYCLE, expectedValues, nullOrToString(run.getScoreCycle()));
    assertAttribute(Field.PAIRED_END, expectedValues, Boolean.toString(run.getPairedEnd()));
  }
}
