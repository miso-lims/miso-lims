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
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.SampleHandsOnTable;

public class BulkSampleEditIT extends AbstractBulkSampleIT {

  private static final Set<String> identityColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.ID_BARCODE, SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.EXTERNAL_NAME, SamColumns.DONOR_SEX,
      SamColumns.SAMPLE_CLASS, SamColumns.GROUP_ID, SamColumns.GROUP_DESCRIPTION, SamColumns.QC_STATUS, SamColumns.QC_NOTE);

  private static final Set<String> tissueColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.RECEIVE_DATE, SamColumns.ID_BARCODE, SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.SAMPLE_CLASS,
      SamColumns.GROUP_ID, SamColumns.GROUP_DESCRIPTION, SamColumns.TISSUE_ORIGIN, SamColumns.TISSUE_TYPE, SamColumns.PASSAGE_NUMBER,
      SamColumns.TIMES_RECEIVED,
      SamColumns.TUBE_NUMBER, SamColumns.LAB, SamColumns.SECONDARY_ID, SamColumns.TISSUE_MATERIAL, SamColumns.REGION, SamColumns.QC_STATUS,
      SamColumns.QC_NOTE);

  private static final Set<String> slideColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.RECEIVE_DATE, SamColumns.ID_BARCODE, SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.SAMPLE_CLASS,
      SamColumns.GROUP_ID, SamColumns.GROUP_DESCRIPTION, SamColumns.SLIDES, SamColumns.DISCARDS, SamColumns.THICKNESS, SamColumns.STAIN,
      SamColumns.QC_STATUS, SamColumns.QC_NOTE);

  private static final Set<String> curlsColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.RECEIVE_DATE, SamColumns.ID_BARCODE, SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.SAMPLE_CLASS, SamColumns.GROUP_ID,
      SamColumns.GROUP_DESCRIPTION, SamColumns.QC_STATUS, SamColumns.QC_NOTE);

  private static final Set<String> lcmTubeColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.RECEIVE_DATE, SamColumns.ID_BARCODE, SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.SAMPLE_CLASS, SamColumns.GROUP_ID,
      SamColumns.GROUP_DESCRIPTION, SamColumns.SLIDES_CONSUMED, SamColumns.QC_STATUS, SamColumns.QC_NOTE);

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
    editable.put(SamColumns.ALIAS, "TEST_0005");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.ID_BARCODE, "changed");
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.EXTERNAL_NAME, "changed");
    editable.put(SamColumns.DONOR_SEX, "Unspecified");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.QC_STATUS, "Refused Consent");

    editable.forEach((k, v) -> table.enterText(k, 0, v));
    editable.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    assertFalse(table.isWritable(SamColumns.NAME, 0));
    assertFalse(table.isWritable(SamColumns.SAMPLE_CLASS, 0));
    assertFalse(table.isWritable(SamColumns.QC_NOTE, 0));

    // ensure dependent columns update properly
    editable.put(SamColumns.QC_STATUS, "OKd by Collaborator");
    table.enterText(SamColumns.QC_STATUS, 0, editable.get(SamColumns.QC_STATUS));
    assertEquals(editable.get(SamColumns.QC_STATUS), table.getText(SamColumns.QC_STATUS, 0));
    editable.put(SamColumns.QC_NOTE, "Approved");
    table.enterText(SamColumns.QC_NOTE, 0, editable.get(SamColumns.QC_NOTE));
    assertEquals(editable.get(SamColumns.QC_NOTE), table.getText(SamColumns.QC_NOTE, 0));
  }

  @Test
  public void testEditSaveEmptySaveIdentity() throws Exception {
    // Goal: ensure all identity fields can be changed and that these changes will be persisted.
    BulkSamplePage page = getEditPage(Arrays.asList(getSampleId("Identity")));
    SampleHandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002");
    editable.put(SamColumns.DESCRIPTION, "new description");
    editable.put(SamColumns.ID_BARCODE, "MATRIX");
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Mus musculus");
    editable.put(SamColumns.EXTERNAL_NAME, "completely new");
    editable.put(SamColumns.DONOR_SEX, "Male");
    editable.put(SamColumns.GROUP_ID, "500");
    editable.put(SamColumns.GROUP_DESCRIPTION, "miles");
    editable.put(SamColumns.QC_STATUS, "OKd by Collaborator");
    editable.put(SamColumns.QC_NOTE, "Approved");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForIdentity(editable, getIdForRow(table, 0), false);

    // reload the page and edit again, setting optional fields to empty
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Identity")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.ID_BARCODE, "");
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
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-5");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.RECEIVE_DATE, "2015-07-17");
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
    editable.put(SamColumns.LAB, "Pathology (University Health Network)");
    editable.put(SamColumns.SECONDARY_ID, "changed");
    editable.put(SamColumns.TISSUE_MATERIAL, "Blood");
    editable.put(SamColumns.REGION, "changed");
    editable.put(SamColumns.QC_STATUS, "Refused Consent");

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
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2");
    editable.put(SamColumns.DESCRIPTION, "changed description");
    editable.put(SamColumns.RECEIVE_DATE, "2016-07-17");
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
    editable.put(SamColumns.LAB, "BioBank (University Health Network)");
    editable.put(SamColumns.SECONDARY_ID, "changed tube");
    editable.put(SamColumns.TISSUE_MATERIAL, "Blood");
    editable.put(SamColumns.REGION, "Pancreatic duct");
    editable.put(SamColumns.QC_STATUS, "Waiting: Receive Tissue");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForTissue(editable, getIdForRow(table, 0), false);

    // now, reload the page and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Tissue")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.RECEIVE_DATE, "");
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
    empty.put(SamColumns.LAB, "(None)");
    empty.put(SamColumns.SECONDARY_ID, "");
    empty.put(SamColumns.TISSUE_MATERIAL, "(None)");
    empty.put(SamColumns.REGION, "");
    empty.put(SamColumns.QC_STATUS, "Not Ready");

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
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL05");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.RECEIVE_DATE, "2015-06-17");
    editable.put(SamColumns.ID_BARCODE, "changed"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.SLIDES, "1700");
    editable.put(SamColumns.DISCARDS, "300");
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
    SampleHandsOnTable table = page.getTable();

    Map<String, String> editable = new HashMap<>();
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL01");
    editable.put(SamColumns.DESCRIPTION, "different");
    editable.put(SamColumns.RECEIVE_DATE, "2017-06-17");
    editable.put(SamColumns.ID_BARCODE, "4003"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Panthera tigris altaica");
    editable.put(SamColumns.GROUP_ID, "12");
    editable.put(SamColumns.GROUP_DESCRIPTION, "different");
    editable.put(SamColumns.SLIDES, "17");
    editable.put(SamColumns.DISCARDS, "3");
    editable.put(SamColumns.THICKNESS, "7");
    editable.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForSlide(editable, getIdForRow(table, 0), false);

    // get the page again, and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Slide")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_SL01");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.RECEIVE_DATE, "");
    empty.put(SamColumns.ID_BARCODE, ""); // increment
    empty.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(SamColumns.SCIENTIFIC_NAME, "Panthera tigris altaica");
    empty.put(SamColumns.GROUP_ID, "");
    empty.put(SamColumns.GROUP_DESCRIPTION, "");
    empty.put(SamColumns.SLIDES, "17");
    empty.put(SamColumns.DISCARDS, "3");
    empty.put(SamColumns.THICKNESS, "");
    empty.put(SamColumns.STAIN, "(None)");
    empty.put(SamColumns.QC_STATUS, "Not Ready");

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
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_C05");
    editable.put(SamColumns.DESCRIPTION, "changed");
    editable.put(SamColumns.RECEIVE_DATE, "2015-06-17");
    editable.put(SamColumns.ID_BARCODE, "changed"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "changed");
    editable.put(SamColumns.GROUP_ID, "changed");
    editable.put(SamColumns.GROUP_DESCRIPTION, "changed");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");

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
    editable.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_C01");
    editable.put(SamColumns.DESCRIPTION, "different");
    editable.put(SamColumns.RECEIVE_DATE, "2017-06-17");
    editable.put(SamColumns.ID_BARCODE, "4003"); // increment
    editable.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    editable.put(SamColumns.SCIENTIFIC_NAME, "Rattus rat");
    editable.put(SamColumns.GROUP_ID, "12");
    editable.put(SamColumns.GROUP_DESCRIPTION, "different");
    editable.put(SamColumns.QC_STATUS, "Waiting: Path Report");

    // make the changes
    editable.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    // verify that the changes have been saved
    assertAllForTissueProcessing(editable, getIdForRow(table, 0), false);

    // get the page again, and empty all optional fields
    BulkSamplePage newPage = getEditPage(Arrays.asList(getSampleId("Curls")));
    SampleHandsOnTable newTable = newPage.getTable();

    Map<String, String> empty = new HashMap<>();
    empty.put(SamColumns.ALIAS, "TEST_0002_Pa_M_13_2-2_C01");
    empty.put(SamColumns.DESCRIPTION, "");
    empty.put(SamColumns.RECEIVE_DATE, "");
    empty.put(SamColumns.ID_BARCODE, ""); // increment
    empty.put(SamColumns.SAMPLE_TYPE, "METAGENOMIC");
    empty.put(SamColumns.SCIENTIFIC_NAME, "Rattus rat");
    empty.put(SamColumns.GROUP_ID, "");
    empty.put(SamColumns.GROUP_DESCRIPTION, "");
    empty.put(SamColumns.QC_STATUS, "Not Ready");

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
