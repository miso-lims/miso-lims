package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils;

public class BulkSampleEditIT extends AbstractBulkSampleIT {

  private static final Set<String> commonColumns =
      Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
          SamColumns.SAMPLE_TYPE, SamColumns.PROJECT, SamColumns.SCIENTIFIC_NAME, SamColumns.SUBPROJECT,
          SamColumns.SAMPLE_CLASS,
          SamColumns.GROUP_ID, SamColumns.GROUP_DESCRIPTION, SamColumns.CREATION_DATE, SamColumns.QC_STATUS,
          SamColumns.QC_NOTE);

  private static final Set<String> boxableColumns =
      Sets.newHashSet(SamColumns.ID_BARCODE, SamColumns.BOX_SEARCH, SamColumns.BOX_ALIAS,
          SamColumns.BOX_POSITION, SamColumns.DISCARDED, SamColumns.EFFECTIVE_GROUP_ID);

  private static final Set<String> identityColumns =
      Sets.newHashSet(SamColumns.EXTERNAL_NAME, SamColumns.DONOR_SEX, SamColumns.CONSENT);

  private static final Set<String> nonIdentityColumns = Sets.newHashSet(SamColumns.PARENT_NAME, SamColumns.PARENT_ALIAS,
      SamColumns.PARENT_LOCATION, SamColumns.REQUISITION_ASSAY);

  private static final Set<String> tissueColumns = Sets.newHashSet(SamColumns.TISSUE_ORIGIN, SamColumns.TISSUE_TYPE,
      SamColumns.PASSAGE_NUMBER, SamColumns.TIMES_RECEIVED, SamColumns.TUBE_NUMBER, SamColumns.LAB,
      SamColumns.SECONDARY_ID,
      SamColumns.TISSUE_MATERIAL, SamColumns.REGION, SamColumns.TIMEPOINT);

  private static final Set<String> slideColumns =
      Sets.newHashSet(SamColumns.SOP, SamColumns.PROBES, SamColumns.INITIAL_SLIDES, SamColumns.SLIDES,
          SamColumns.THICKNESS, SamColumns.STAIN, SamColumns.PERCENT_TUMOUR, SamColumns.PERCENT_NECROSIS,
          SamColumns.MARKED_AREA, SamColumns.MARKED_AREA_PERCENT_TUMOUR, SamColumns.INITIAL_VOLUME, SamColumns.VOLUME,
          SamColumns.VOLUME_UNITS, SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS);

  private static final Set<String> tissuePieceColumns =
      Sets.newHashSet(SamColumns.SOP, SamColumns.PROBES, SamColumns.PIECE_TYPE, SamColumns.SLIDES_CONSUMED,
          SamColumns.REFERENCE_SLIDE, SamColumns.INITIAL_VOLUME, SamColumns.VOLUME, SamColumns.VOLUME_UNITS,
          SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS);

  private BulkSamplePage getEditPage(List<Long> ids) {
    return BulkSamplePage.getForEdit(getDriver(), getBaseUrl(), ids);
  }

  @Test
  public void testEditIdentitySetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(commonColumns);
    expectedHeadings.addAll(identityColumns);

    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testEditIdentityFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0005");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.EXTERNAL_NAME, "changed");
    editable.put(SamColumns.DONOR_SEX, "Unspecified");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.QC_STATUS, "OKd by Collaborator");
    editable.put(SamColumns.QC_NOTE, "Approved");
    editable.put(SamColumns.CREATION_DATE, "2018-07-17");

    editable.forEach((k, v) -> table.enterText(k, 0, v));
    editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    assertFalse(table.isWritable(SamColumns.NAME, 0));
    assertFalse(table.isWritable(SamColumns.SAMPLE_CLASS, 0));
  }

  @Test
  public void testEditSaveEmptySaveIdentity() throws Exception {
    // Goal: ensure all identity fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002");
    editable.put(SamColumns.DESCRIPTION, "new description");
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Mus musculus");
    editable.put(SamColumns.EXTERNAL_NAME, "completely new");
    editable.put(SamColumns.DONOR_SEX, "Male");
    editable.put(SamColumns.GROUP_ID, "500");
    editable.put(SamColumns.GROUP_DESCRIPTION, "miles");
    editable.put(SamColumns.QC_STATUS, "OKd by Collaborator");
    editable.put(SamColumns.QC_NOTE, "Approved");
    editable.put(SamColumns.CREATION_DATE, "");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify that the changes have been saved
    assertAllForIdentity(editable, getIdForRow(savedTable, 0), false);

    // reload the page and edit again, setting optional fields to empty
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Identity")));
    HandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(SamColumns.SCIENTIFIC_NAME, "Mus musculus");
    empty.put(SamColumns.EXTERNAL_NAME, "completely new");
    empty.put(SamColumns.DONOR_SEX, "Unknown");
    empty.put(SamColumns.GROUP_ID, "");
    empty.put(SamColumns.GROUP_DESCRIPTION, "");
    empty.put(SamColumns.QC_STATUS, "Not Ready");
    empty.put(SamColumns.QC_NOTE, "");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    assertTrue(newPage.save(false));
    HandsOnTable newSavedTable = newPage.getTable();

    // verify that the changes have been saved
    assertAllForIdentity(empty, getIdForRow(newSavedTable, 0), false);
  }

  @Test
  public void testEditTissueSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(commonColumns);
    expectedHeadings.addAll(boxableColumns);
    expectedHeadings.addAll(nonIdentityColumns);
    expectedHeadings.addAll(tissueColumns);

    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue")));
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testEditTissueFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-5");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.ID_BARCODE, "changed"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.TISSUE_ORIGIN, "Ly (Lymphocyte)");
    editable.put(SamColumns.TISSUE_TYPE, "T (Unclassified tumour)");
    editable.put(SamColumns.PASSAGE_NUMBER, "1300");
    editable.put(SamColumns.TIMES_RECEIVED, "200");
    editable.put(SamColumns.TUBE_NUMBER, "200");
    editable.put(SamColumns.LAB, "BioBank (University Health Network)");
    editable.put(SamColumns.SECONDARY_ID, "changed");
    editable.put(SamColumns.TISSUE_MATERIAL, "Blood");
    editable.put(SamColumns.REGION, "changed");
    editable.put(SamColumns.QC_STATUS, "Refused Consent");

    // assert not equals to start
    editable.forEach((k, v) -> assertNotEquals(v, table.getText(k, 0)));
    assertNotEquals("changed", table.getText(SamColumns.EFFECTIVE_GROUP_ID, 0));
    // make the changes
    editable.remove(SamColumns.EFFECTIVE_GROUP_ID); // should be auto-updated
    editable.forEach((k, v) -> table.enterText(k, 0, v));
    // assert that the changes have been made
    editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    // ensure dependent columns update properly
    assertEquals("changed", table.getText(SamColumns.EFFECTIVE_GROUP_ID, 0));
    // no tissue-specific dependent columns at this time
  }

  @Test
  public void testEditSaveEmptySaveTissue() throws Exception {
    // Goal: ensure all tissue fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2");
    editable.put(SamColumns.DESCRIPTION, "changed description");
    editable.put(SamColumns.ID_BARCODE, "3001"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Mus musculus");
    editable.put(SamColumns.GROUP_ID, "2");
    editable.put(SamColumns.GROUP_DESCRIPTION, "Test two");
    editable.put(SamColumns.TISSUE_ORIGIN, "Pa (Pancreas)");
    editable.put(SamColumns.TISSUE_TYPE, "M (Metastatic tumour)");
    editable.put(SamColumns.PASSAGE_NUMBER, "13");
    editable.put(SamColumns.TIMES_RECEIVED, "2");
    editable.put(SamColumns.TUBE_NUMBER, "2");
    editable.put(SamColumns.LAB, "University Health Network - BioBank");
    editable.put(SamColumns.SECONDARY_ID, "changed tube");
    editable.put(SamColumns.TISSUE_MATERIAL, "Blood");
    editable.put(SamColumns.REGION, "Pancreatic duct");
    editable.put(SamColumns.QC_STATUS, "Waiting: Receive Tissue");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    editable.put(SamColumns.BOX_ALIAS, "Boxxy");
    editable.put(SamColumns.BOX_POSITION, "A01");
    table.enterText(SamColumns.BOX_SEARCH, 0, editable.get(SamColumns.BOX_ALIAS));
    table.enterText(SamColumns.BOX_POSITION, 0, editable.get(SamColumns.BOX_POSITION));

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify that the changes have been saved
    assertAllForTissue(editable, getIdForRow(savedTable, 0), false);

    // now, reload the page and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Tissue")));
    HandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.ID_BARCODE, ""); // increment
    empty.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(SamColumns.SCIENTIFIC_NAME, "Mus musculus");
    empty.put(SamColumns.GROUP_ID, "");
    empty.put(SamColumns.GROUP_DESCRIPTION, "");
    empty.put(SamColumns.TISSUE_ORIGIN, "Pa (Pancreas)");
    empty.put(SamColumns.TISSUE_TYPE, "M (Metastatic tumour)");
    empty.put(SamColumns.PASSAGE_NUMBER, "");
    empty.put(SamColumns.TIMES_RECEIVED, "2");
    empty.put(SamColumns.TUBE_NUMBER, "2");
    empty.put(SamColumns.LAB, "");
    empty.put(SamColumns.SECONDARY_ID, "");
    empty.put(SamColumns.TISSUE_MATERIAL, "");
    empty.put(SamColumns.REGION, "");
    empty.put(SamColumns.QC_STATUS, "Not Ready");
    empty.put(SamColumns.BOX_ALIAS, "");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    empty.put(SamColumns.BOX_POSITION, "");

    assertTrue(newPage.save(false));
    HandsOnTable newSavedTable = newPage.getTable();

    // verify that the changes have been saved
    assertAllForTissue(empty, getIdForRow(newSavedTable, 0), false);
  }

  @Test
  public void testEditSlideSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(commonColumns);
    expectedHeadings.addAll(boxableColumns);
    expectedHeadings.addAll(nonIdentityColumns);
    expectedHeadings.addAll(slideColumns);

    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Slide")));
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testEditSlideFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Slide")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL05");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.ID_BARCODE, "changed"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.SLIDES, "1700");
    editable.put(SamColumns.THICKNESS, "700");
    editable.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");

    // assert not equals to start
    editable.forEach((k, v) -> assertNotEquals(v, table.getText(k, 0)));
    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));
    // assert that the changes have been made
    editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    // ensure dependent columns update properly
    // no slide-specific dependent columns at this time
  }

  @Test
  public void testEditSaveEmptySaveSlide() throws Exception {
    // Goal: ensure all Slide fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Slide")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL01");
    editable.put(SamColumns.DESCRIPTION, "different");
    editable.put(SamColumns.ID_BARCODE, "4003"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Panthera tigris altaica");
    editable.put(SamColumns.GROUP_ID, "12");
    editable.put(SamColumns.GROUP_DESCRIPTION, "different");
    editable.put(SamColumns.SLIDES, "17");
    editable.put(SamColumns.THICKNESS, "7");
    editable.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify that the changes have been saved
    assertAllForSlide(editable, getIdForRow(savedTable, 0), false);

    // get the page again, and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Slide")));
    HandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL01");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.ID_BARCODE, ""); // increment
    empty.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(SamColumns.SCIENTIFIC_NAME, "Panthera tigris altaica");
    empty.put(SamColumns.GROUP_ID, "");
    empty.put(SamColumns.GROUP_DESCRIPTION, "");
    empty.put(SamColumns.SLIDES, "17");
    empty.put(SamColumns.THICKNESS, "");
    empty.put(SamColumns.STAIN, "");
    empty.put(SamColumns.QC_STATUS, "Not Ready");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    assertTrue(newPage.save(false));
    HandsOnTable newSavedTable = newPage.getTable();

    // verify that the changes have been saved
    assertAllForSlide(empty, getIdForRow(newSavedTable, 0), false);
  }

  @Test
  public void testEditTissuePieceFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Curls")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_C05");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.ID_BARCODE, "changed"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");
    editable.put(SamColumns.PIECE_TYPE, "LCM Tube");
    editable.put(SamColumns.SLIDES_CONSUMED, "2");
    editable.put(SamColumns.REFERENCE_SLIDE, "SAM3 (TEST_0001_Bn_R_nn_1-1_SL01)");

    // assert not equals to start
    editable.forEach((k, v) -> assertNotEquals(v, table.getText(k, 0)));
    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));
    // assert that the changes have been made
    editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    // ensure dependent columns update properly
    // no curls-specific dependent columns at this time
  }

  @Test
  public void testEditSaveEmptySaveCurls() throws Exception {
    // Goal: ensure all Curls fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Curls")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_C01");
    editable.put(SamColumns.DESCRIPTION, "different");
    editable.put(SamColumns.ID_BARCODE, "4003"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Rattus rat");
    editable.put(SamColumns.GROUP_ID, "12");
    editable.put(SamColumns.GROUP_DESCRIPTION, "different");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify that the changes have been saved
    assertAllForTissueProcessing(editable, getIdForRow(savedTable, 0), false);

    // get the page again, and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Curls")));
    HandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_C01");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.ID_BARCODE, ""); // increment
    empty.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(SamColumns.SCIENTIFIC_NAME, "Rattus rat");
    empty.put(SamColumns.GROUP_ID, "");
    empty.put(SamColumns.GROUP_DESCRIPTION, "");
    empty.put(SamColumns.QC_STATUS, "Not Ready");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    assertTrue(newPage.save(false));
    HandsOnTable newSavedTable = newPage.getTable();

    // verify that the changes have been saved
    assertAllForTissueProcessing(empty, getIdForRow(newSavedTable, 0), false);
  }

  @Test
  public void testEditTissuePieceSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(commonColumns);
    expectedHeadings.addAll(boxableColumns);
    expectedHeadings.addAll(nonIdentityColumns);
    expectedHeadings.addAll(tissuePieceColumns);

    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue Piece")));
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  public Long getSampleId(String sampleClass) {
    Map<String, Long> sampleIds = new HashMap<>();
    sampleIds.put("Identity", 1L);
    sampleIds.put("Tissue", 2L);
    sampleIds.put("Slide", 3L);
    sampleIds.put("Curls", 4L);
    sampleIds.put("Tissue Piece", 5L);
    sampleIds.put("gDNA (stock)", 6L);
    sampleIds.put("whole RNA (stock)", 7L);
    sampleIds.put("gDNA (aliquot)", 8L);
    sampleIds.put("whole RNA (aliquot)", 9L);
    sampleIds.put("cDNA (stock)", 10L);
    sampleIds.put("cDNA (aliquot)", 11L);
    sampleIds.put("smRNA", 12L);
    sampleIds.put("mRNA", 13L);
    sampleIds.put("rRNA_depeletd", 14L);

    if (sampleIds.get(sampleClass) != null) {
      return sampleIds.get(sampleClass);
    } else {
      throw new IllegalArgumentException("Misspelling?");
    }
  }

}
