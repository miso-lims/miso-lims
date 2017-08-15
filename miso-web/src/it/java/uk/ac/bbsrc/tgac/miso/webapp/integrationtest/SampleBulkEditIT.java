package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.SampleHandsOnTable;

public class SampleBulkEditIT extends AbstractBulkSampleIT {

  private static final Set<String> identityColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.EXTERNAL_NAME, Columns.DONOR_SEX,
      Columns.SAMPLE_CLASS, Columns.GROUP_ID, Columns.GROUP_DESCRIPTION, Columns.QC_STATUS, Columns.QC_NOTE);

  private static final Set<String> tissueColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.SAMPLE_CLASS,
      Columns.GROUP_ID, Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER,
      Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.QC_STATUS,
      Columns.QC_NOTE);

  private static final Set<String> slideColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.SAMPLE_CLASS,
      Columns.GROUP_ID, Columns.GROUP_DESCRIPTION, Columns.SLIDES, Columns.DISCARDS, Columns.THICKNESS, Columns.STAIN,
      Columns.QC_STATUS, Columns.QC_NOTE);

  private static final Set<String> curlsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.QC_STATUS, Columns.QC_NOTE);

  private static final Set<String> lcmTubeColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.SLIDES_CONSUMED, Columns.QC_STATUS, Columns.QC_NOTE);

  @Before
  public void setup() {
    loginAdmin();
  }

  private BulkSamplePage getEditPage(List<Long> ids) {
    return BulkSamplePage.getForEdit(getDriver(), getBaseUrl(), ids);
  }

  @Test
  public void testEditIdentitySetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(identityColumns.size(), headings.size());
    for (String col : identityColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testEditIdentityFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0005");
    editable.put(Columns.DESCRIPTION, "changed");
    editable.put(Columns.ID_BARCODE, "changed");
    editable.put(Columns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "changed");
    editable.put(Columns.EXTERNAL_NAME, "changed");
    editable.put(Columns.DONOR_SEX, "Unspecified");
    editable.put(Columns.GROUP_ID, "changed");
    editable.put(Columns.GROUP_DESCRIPTION, "changed");
    editable.put(Columns.QC_STATUS, "Refused Consent");

    editable.forEach((k, v) -> table.enterText(k, 0, v));
    editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    assertFalse(table.isWritable(Columns.NAME, 0));
    assertFalse(table.isWritable(Columns.SAMPLE_CLASS, 0));
    assertFalse(table.isWritable(Columns.QC_NOTE, 0));

    // ensure dependent columns update properly
    editable.put(Columns.QC_STATUS, "OKd by Collaborator");
    table.enterText(Columns.QC_STATUS, 0, editable.get(Columns.QC_STATUS));
    assertEquals(editable.get(Columns.QC_STATUS), table.getText(Columns.QC_STATUS, 0));
    editable.put(Columns.QC_NOTE, "Approved");
    table.enterText(Columns.QC_NOTE, 0, editable.get(Columns.QC_NOTE));
    assertEquals(editable.get(Columns.QC_NOTE), table.getText(Columns.QC_NOTE, 0));
  }

  @Test
  public void testEditSaveEmptySaveIdentity() throws Exception {
    // Goal: ensure all identity fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    SampleHandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002");
    editable.put(Columns.DESCRIPTION, "new description");
    editable.put(Columns.ID_BARCODE, "MATRIX");
    editable.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "Mus musculus");
    editable.put(Columns.EXTERNAL_NAME, "completely new");
    editable.put(Columns.DONOR_SEX, "Male");
    editable.put(Columns.GROUP_ID, "500");
    editable.put(Columns.GROUP_DESCRIPTION, "miles");
    editable.put(Columns.QC_STATUS, "OKd by Collaborator");
    editable.put(Columns.QC_NOTE, "Approved");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForIdentity(editable, getIdForRow(table, 0), false);

    // reload the page and edit again, setting optional fields to empty
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Identity")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(Columns.ALIAS, "TEST_0002");
    empty.put(Columns.DESCRIPTION, "");
    empty.put(Columns.ID_BARCODE, "");
    empty.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(Columns.SCIENTIFIC_NAME, "Mus musculus");
    empty.put(Columns.EXTERNAL_NAME, "completely new");
    empty.put(Columns.DONOR_SEX, "Unknown");
    empty.put(Columns.GROUP_ID, "");
    empty.put(Columns.GROUP_DESCRIPTION, "");
    empty.put(Columns.QC_STATUS, "Not Ready");
    empty.put(Columns.QC_NOTE, "");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    saveSingleAndAssertSuccess(newTable);

    // verify that the changes have been saved
    assertAllForIdentity(empty, getIdForRow(newTable, 0), false);
  }

  @Test
  public void testEditTissueSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue")));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(tissueColumns.size(), headings.size());
    for (String col : tissueColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testEditTissueFields() throws Exception {
   // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue")));
   HandsOnTable table = page.getTable();

   Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-5");
    editable.put(Columns.DESCRIPTION, "changed");
    editable.put(Columns.RECEIVE_DATE, "2015-07-17");
    editable.put(Columns.ID_BARCODE, "changed"); // increment
    editable.put(Columns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "changed");
    editable.put(Columns.GROUP_ID, "changed");
    editable.put(Columns.GROUP_DESCRIPTION, "changed");
    editable.put(Columns.TISSUE_ORIGIN, "Ly (Lymphocyte)");
    editable.put(Columns.TISSUE_TYPE, "T (Unclassified tumour)");
    editable.put(Columns.PASSAGE_NUMBER, "1300");
    editable.put(Columns.TIMES_RECEIVED, "200");
    editable.put(Columns.TUBE_NUMBER, "200");
    editable.put(Columns.LAB, "Pathology (University Health Network)");
    editable.put(Columns.EXT_INST_ID, "changed");
   editable.put(Columns.TISSUE_MATERIAL, "Blood");
    editable.put(Columns.REGION, "changed");
    editable.put(Columns.QC_STATUS, "Refused Consent");

   // assert not equals to start
   editable.forEach((k, v) -> assertNotEquals(v, table.getText(k, 0)));
   // make the changes
   editable.forEach((k, v) -> table.enterText(k, 0, v));
   // assert that the changes have been made
   editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
   
   // ensure dependent columns update properly
   // no tissue-specific dependent columns at this time
  }
  
  @Test
  public void testEditSaveEmptySaveTissue() throws Exception {
    // Goal: ensure all tissue fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Tissue")));
    SampleHandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2");
    editable.put(Columns.DESCRIPTION, "changed description");
    editable.put(Columns.RECEIVE_DATE, "2016-07-17");
    editable.put(Columns.ID_BARCODE, "3001"); // increment
    editable.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "Mus musculus");
    editable.put(Columns.GROUP_ID, "2");
    editable.put(Columns.GROUP_DESCRIPTION, "Test two");
    editable.put(Columns.TISSUE_ORIGIN, "Pa (Pancreas)");
    editable.put(Columns.TISSUE_TYPE, "M (Metastatic tumour)");
    editable.put(Columns.PASSAGE_NUMBER, "13");
    editable.put(Columns.TIMES_RECEIVED, "2");
    editable.put(Columns.TUBE_NUMBER, "2");
    editable.put(Columns.LAB, "BioBank (University Health Network)");
    editable.put(Columns.EXT_INST_ID, "changed tube");
    editable.put(Columns.TISSUE_MATERIAL, "Blood");
    editable.put(Columns.REGION, "Pancreatic duct");
    editable.put(Columns.QC_STATUS, "Waiting: Receive Tissue");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForTissue(editable, getIdForRow(table, 0), false);

    // now, reload the page and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Tissue")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2");
    empty.put(Columns.DESCRIPTION, "");
    empty.put(Columns.RECEIVE_DATE, "");
    empty.put(Columns.ID_BARCODE, ""); // increment
    empty.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(Columns.SCIENTIFIC_NAME, "Mus musculus");
    empty.put(Columns.GROUP_ID, "");
    empty.put(Columns.GROUP_DESCRIPTION, "");
    empty.put(Columns.TISSUE_ORIGIN, "Pa (Pancreas)");
    empty.put(Columns.TISSUE_TYPE, "M (Metastatic tumour)");
    empty.put(Columns.PASSAGE_NUMBER, "");
    empty.put(Columns.TIMES_RECEIVED, "2");
    empty.put(Columns.TUBE_NUMBER, "2");
    empty.put(Columns.LAB, "(None)");
    empty.put(Columns.EXT_INST_ID, "");
    empty.put(Columns.TISSUE_MATERIAL, "(None)");
    empty.put(Columns.REGION, "");
    empty.put(Columns.QC_STATUS, "Not Ready");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    saveSingleAndAssertSuccess(newTable);

    // verify that the changes have been saved
    assertAllForTissue(empty, getIdForRow(newTable, 0), false);
  }

  @Test
  public void testEditSlideSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Slide")));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(slideColumns.size(), headings.size());
    for (String col : slideColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testEditSlideFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Slide")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL05");
    editable.put(Columns.DESCRIPTION, "changed");
    editable.put(Columns.RECEIVE_DATE, "2015-06-17");
    editable.put(Columns.ID_BARCODE, "changed"); // increment
    editable.put(Columns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "changed");
    editable.put(Columns.GROUP_ID, "changed");
    editable.put(Columns.GROUP_DESCRIPTION, "changed");
    editable.put(Columns.SLIDES, "1700");
    editable.put(Columns.DISCARDS, "300");
    editable.put(Columns.THICKNESS, "700");
    editable.put(Columns.STAIN, "Hematoxylin+Eosin");
    editable.put(Columns.QC_STATUS, "Waiting: Path Report");

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
    SampleHandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL01");
    editable.put(Columns.DESCRIPTION, "different");
    editable.put(Columns.RECEIVE_DATE, "2017-06-17");
    editable.put(Columns.ID_BARCODE, "4003"); // increment
    editable.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "Panthera tigris altaica");
    editable.put(Columns.GROUP_ID, "12");
    editable.put(Columns.GROUP_DESCRIPTION, "different");
    editable.put(Columns.SLIDES, "17");
    editable.put(Columns.DISCARDS, "3");
    editable.put(Columns.THICKNESS, "7");
    editable.put(Columns.STAIN, "Hematoxylin+Eosin");
    editable.put(Columns.QC_STATUS, "Waiting: Path Report");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForSlide(editable, getIdForRow(table, 0), false);

    // get the page again, and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Slide")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL01");
    empty.put(Columns.DESCRIPTION, "");
    empty.put(Columns.RECEIVE_DATE, "");
    empty.put(Columns.ID_BARCODE, ""); // increment
    empty.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(Columns.SCIENTIFIC_NAME, "Panthera tigris altaica");
    empty.put(Columns.GROUP_ID, "");
    empty.put(Columns.GROUP_DESCRIPTION, "");
    empty.put(Columns.SLIDES, "17");
    empty.put(Columns.DISCARDS, "3");
    empty.put(Columns.THICKNESS, "");
    empty.put(Columns.STAIN, "(None)");
    empty.put(Columns.QC_STATUS, "Not Ready");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    saveSingleAndAssertSuccess(newTable);

    // verify that the changes have been saved
    assertAllForSlide(empty, getIdForRow(newTable, 0), false);
  }

  @Test
  public void testEditCurlsSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Curls")));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(curlsColumns.size(), headings.size());
    for (String col : curlsColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testEditCurlsFields() throws Exception {
    // Goal: ensure all editable fields can be changed, and all readOnly fields can not.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Curls")));
    HandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2_C05");
    editable.put(Columns.DESCRIPTION, "changed");
    editable.put(Columns.RECEIVE_DATE, "2015-06-17");
    editable.put(Columns.ID_BARCODE, "changed"); // increment
    editable.put(Columns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "changed");
    editable.put(Columns.GROUP_ID, "changed");
    editable.put(Columns.GROUP_DESCRIPTION, "changed");
    editable.put(Columns.QC_STATUS, "Waiting: Path Report");

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
    SampleHandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2_C01");
    editable.put(Columns.DESCRIPTION, "different");
    editable.put(Columns.RECEIVE_DATE, "2017-06-17");
    editable.put(Columns.ID_BARCODE, "4003"); // increment
    editable.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(Columns.SCIENTIFIC_NAME, "Rattus rat");
    editable.put(Columns.GROUP_ID, "12");
    editable.put(Columns.GROUP_DESCRIPTION, "different");
    editable.put(Columns.QC_STATUS, "Waiting: Path Report");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForTissueProcessing(editable, getIdForRow(table, 0), false);

    // get the page again, and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Curls")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(Columns.ALIAS, "TEST_0002_Pa_M_13_2-2_C01");
    empty.put(Columns.DESCRIPTION, "");
    empty.put(Columns.RECEIVE_DATE, "");
    empty.put(Columns.ID_BARCODE, ""); // increment
    empty.put(Columns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(Columns.SCIENTIFIC_NAME, "Rattus rat");
    empty.put(Columns.GROUP_ID, "");
    empty.put(Columns.GROUP_DESCRIPTION, "");
    empty.put(Columns.QC_STATUS, "Not Ready");

    // make the changes
    empty.forEach((k, v) -> newTable.enterText(k, 0, v));

    saveSingleAndAssertSuccess(newTable);

    // verify that the changes have been saved
    assertAllForTissueProcessing(empty, getIdForRow(newTable, 0), false);
  }

  @Test
  public void testEditLcmTubeSetup() throws Exception {

  }

  public Long getSampleId(String sampleClass) {
    Map<String, Long> sampleIds = new HashMap<>();
    sampleIds.put("Identity", 1L);
    sampleIds.put("Tissue", 2L);
    sampleIds.put("Slide", 3L);
    sampleIds.put("Curls", 4L);
    sampleIds.put("LCM Tube", 5L);
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
