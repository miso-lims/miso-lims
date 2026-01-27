package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleSlideImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueProcessingImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils;

public class BulkSampleCreateIT extends AbstractBulkSampleIT {

  // columns for creating Identity and everything else
  private static final Set<String> identityColumns =
      Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
          SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.PROJECT, SamColumns.EXTERNAL_NAME,
          SamColumns.DONOR_SEX,
          SamColumns.CONSENT, SamColumns.SUBPROJECT, SamColumns.SAMPLE_CLASS, SamColumns.GROUP_ID,
          SamColumns.GROUP_DESCRIPTION,
          SamColumns.CREATION_DATE, SamColumns.QC_STATUS, SamColumns.QC_NOTE);

  // columns for creating Tissue and everything downstream of it
  private static final Set<String> tissueColumns = Sets.newHashSet(SamColumns.ID_BARCODE, SamColumns.BOX_SEARCH,
      SamColumns.BOX_ALIAS,
      SamColumns.BOX_POSITION, SamColumns.DISCARDED, SamColumns.RECEIVE_DATE, SamColumns.RECEIVE_TIME,
      SamColumns.RECEIVED_FROM,
      SamColumns.RECEIVED_BY, SamColumns.RECEIPT_CONFIRMED, SamColumns.RECEIPT_QC_PASSED, SamColumns.RECEIPT_QC_NOTE,
      SamColumns.REQUISITION_ALIAS, SamColumns.REQUISITION, SamColumns.REQUISITION_ASSAY, SamColumns.IDENTITY_ALIAS,
      SamColumns.TISSUE_ORIGIN, SamColumns.TISSUE_TYPE, SamColumns.PASSAGE_NUMBER, SamColumns.TIMES_RECEIVED,
      SamColumns.TUBE_NUMBER,
      SamColumns.SECONDARY_ID, SamColumns.TISSUE_MATERIAL, SamColumns.REGION, SamColumns.TIMEPOINT);

  private static final Set<String> tissueProcessingDirectColumns = Sets.newHashSet(SamColumns.PROBES);

  // columns specific to creating Slides
  private static final Set<String> slideColumns = Sets.newHashSet(SamColumns.SOP, SamColumns.SLIDES,
      SamColumns.THICKNESS, SamColumns.STAIN, SamColumns.PERCENT_TUMOUR, SamColumns.PERCENT_NECROSIS,
      SamColumns.MARKED_AREA, SamColumns.MARKED_AREA_PERCENT_TUMOUR, SamColumns.VOLUME, SamColumns.VOLUME_UNITS,
      SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS);

  // columns specific to creating curls
  private static final Set<String> tissuePieceColumns =
      Sets.newHashSet(SamColumns.SOP, SamColumns.PIECE_TYPE, SamColumns.SLIDES_CONSUMED, SamColumns.VOLUME,
          SamColumns.VOLUME_UNITS, SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS);

  // columns specific to creating single cells (tissue processing)
  private static final Set<String> singleCellColumns =
      Sets.newHashSet(SamColumns.SOP, SamColumns.INITIAL_CELL_CONC, SamColumns.TARGET_CELL_RECOVERY,
          SamColumns.LOADING_CELL_CONC, SamColumns.DIGESTION, SamColumns.VOLUME, SamColumns.VOLUME_UNITS,
          SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS);

  // columns specific to creating stocks
  private static final Set<String> stockColumns =
      Sets.newHashSet(SamColumns.SOP, SamColumns.STR_STATUS, SamColumns.VOLUME,
          SamColumns.VOLUME_UNITS, SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS, SamColumns.PARENT_NG_USED,
          SamColumns.PARENT_VOLUME_USED);

  // columns specific to creating RNA stocks
  private static final Set<String> rnaStockColumns = Sets.newHashSet(SamColumns.DNASE_TREATED);

  private static final Set<String> singleCellStockColumns =
      Sets.newHashSet(SamColumns.TARGET_CELL_RECOVERY, SamColumns.CELL_VIABILITY,
          SamColumns.LOADING_CELL_CONC, SamColumns.PARENT_NG_USED, SamColumns.PARENT_VOLUME_USED);

  // columns specific to creating aliquots
  private static final Set<String> aliquotColumns =
      Sets.newHashSet(SamColumns.SOP, SamColumns.PURPOSE, SamColumns.PARENT_NG_USED,
          SamColumns.PARENT_VOLUME_USED);

  private static final Set<String> singleCellAliquotColumns =
      Sets.newHashSet(SamColumns.INPUT_INTO_LIBRARY, SamColumns.PARENT_NG_USED,
          SamColumns.PARENT_VOLUME_USED);

  private BulkSamplePage getCreatePage(Integer quantity, Long projectId, String sampleCategory) {
    return BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), quantity, projectId, sampleCategory);
  }

  @Test
  public void testSaveEmptyFail() throws Exception {
    // Goal: ensure save fails and error message is shown when trying to save with required fields empty
    BulkSamplePage page = getCreatePage(1, projectId, SampleAliquot.CATEGORY_NAME);
    assertFalse(page.save(false, true));
  }

  @Test
  public void testCreateTissueSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);

    BulkSamplePage page = getCreatePage(1, null, SampleTissue.CATEGORY_NAME);
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testCreateTissueDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Set<String> sampleTypes = table.getDropdownOptions(SamColumns.SAMPLE_TYPE, 0);
    assertTrue(sampleTypes.size() >= 8);
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(SamColumns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(SamColumns.SAMPLE_TYPE, 0));

    Set<String> projects = table.getDropdownOptions(SamColumns.PROJECT, 0);
    assertTrue(projects.size() > 0);
    assertTrue(projects.contains("PONE"));

    table.enterText(SamColumns.PROJECT, 0, "PONE");
    assertEquals("PONE", table.getText(SamColumns.PROJECT, 0));

    Set<String> donorSexes = table.getDropdownOptions(SamColumns.DONOR_SEX, 0);
    assertTrue(donorSexes.size() >= 5);
    assertTrue(donorSexes.contains("Female"));
    assertTrue(donorSexes.contains("Unspecified"));

    table.enterText(SamColumns.DONOR_SEX, 0, "Unspe");
    assertEquals("Unspecified", table.getText(SamColumns.DONOR_SEX, 0));

    Set<String> tissueOrigins = table.getDropdownOptions(SamColumns.TISSUE_ORIGIN, 0);
    assertTrue(tissueOrigins.size() >= 3);
    assertTrue(tissueOrigins.contains("Bn (Brain)"));
    assertTrue(tissueOrigins.contains("Pa (Pancreas)"));

    table.enterText(SamColumns.TISSUE_ORIGIN, 0, "Ly");
    assertEquals("Ly (Lymphocyte)", table.getText(SamColumns.TISSUE_ORIGIN, 0));

    Set<String> tissueTypes = table.getDropdownOptions(SamColumns.TISSUE_TYPE, 0);
    assertTrue(tissueTypes.size() >= 14);
    assertTrue(tissueTypes.contains("P (Primary tumour)"));
    assertTrue(tissueTypes.contains("n (Unknown)"));

    table.enterText(SamColumns.TISSUE_TYPE, 0, "Benign");
    assertEquals("B (Benign tumour)", table.getText(SamColumns.TISSUE_TYPE, 0));

    Set<String> materials = table.getDropdownOptions(SamColumns.TISSUE_MATERIAL, 0);
    assertTrue(materials.size() >= 3);
    assertTrue(materials.contains("FFPE"));

    table.enterText(SamColumns.TISSUE_MATERIAL, 0, "Fresh");
    assertEquals("Fresh Frozen", table.getText(SamColumns.TISSUE_MATERIAL, 0));

    Set<String> qcStatuses = table.getDropdownOptions(SamColumns.QC_STATUS, 0);
    assertTrue(qcStatuses.size() >= 10);
    assertTrue(qcStatuses.contains("Ready"));
    assertTrue(qcStatuses.contains("Refused Consent"));

    table.enterText(SamColumns.QC_STATUS, 0, "Rea");
    assertEquals("Ready", table.getText(SamColumns.QC_STATUS, 0));
  }

  @Test
  public void testCreateTissueDependencyCells() throws Exception {
    // Goal: ensure that changing the external name value causes the identity alias dropdown to be
    // populated
    BulkSamplePage page = getCreatePage(1, null, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    assertTrue("identity alias is empty", isStringEmptyOrNull(table.getText(SamColumns.IDENTITY_ALIAS, 0)));
    table.enterText(SamColumns.IDENTITY_ALIAS, 0, "Identity 1");
    table.waitForSearch(SamColumns.IDENTITY_ALIAS, 0);
    assertTrue("identity alias no longer empty", !isStringEmptyOrNull(table.getText(SamColumns.IDENTITY_ALIAS, 0)));
  }

  @Test
  public void testCreateOneTissueNoProject() throws Exception {
    // Goal: ensure one tissue can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new LinkedHashMap<>();
    tissue.put(SamColumns.DESCRIPTION, "Description");
    tissue.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    tissue.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    tissue.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    tissue.put(SamColumns.RECEIPT_CONFIRMED, "True");
    tissue.put(SamColumns.RECEIPT_QC_PASSED, "True");
    tissue.put(SamColumns.RECEIPT_QC_NOTE, "");
    tissue.put(SamColumns.PROJECT, "PONE");
    tissue.put(SamColumns.ID_BARCODE, "101"); // increment
    tissue.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(SamColumns.GROUP_ID, "1");
    tissue.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    tissue.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(SamColumns.PASSAGE_NUMBER, "");
    tissue.put(SamColumns.TIMES_RECEIVED, "1");
    tissue.put(SamColumns.TUBE_NUMBER, "1");
    tissue.put(SamColumns.SECONDARY_ID, "tube id 1");
    tissue.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    tissue.put(SamColumns.REGION, "Medulla oblongata");
    tissue.put(SamColumns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(SamColumns.EXTERNAL_NAME, "ext1"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, tissue.get(SamColumns.EXTERNAL_NAME));

    tissue.put(SamColumns.BOX_ALIAS, "Boxxy");
    tissue.put(SamColumns.BOX_POSITION, "A01");
    table.enterText(SamColumns.BOX_SEARCH, 0, tissue.get(SamColumns.BOX_ALIAS));
    table.enterText(SamColumns.BOX_POSITION, 0, tissue.get(SamColumns.BOX_POSITION));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForTissue(tissue, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneTissueWithProject() throws Exception {
    // Goal: ensure one tissue associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new LinkedHashMap<>();
    tissue.put(SamColumns.DESCRIPTION, "Description");
    tissue.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    tissue.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    tissue.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    tissue.put(SamColumns.RECEIPT_CONFIRMED, "True");
    tissue.put(SamColumns.RECEIPT_QC_PASSED, "True");
    tissue.put(SamColumns.RECEIPT_QC_NOTE, "");
    tissue.put(SamColumns.ID_BARCODE, "102"); // increment
    tissue.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(SamColumns.GROUP_ID, "1");
    tissue.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    tissue.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(SamColumns.PASSAGE_NUMBER, "");
    tissue.put(SamColumns.TIMES_RECEIVED, "1");
    tissue.put(SamColumns.TUBE_NUMBER, "1");
    tissue.put(SamColumns.SECONDARY_ID, "tube id 1");
    tissue.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    tissue.put(SamColumns.REGION, "Medulla oblongata");
    tissue.put(SamColumns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(SamColumns.EXTERNAL_NAME, "ext2"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, tissue.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    String newId = savedTable.getText(SamColumns.NAME, 0).substring(3, savedTable.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissue created = (SampleTissue) getSession().get(SampleTissueImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // everything else should be the same as in testCreateOneTissueNoProject() since the `pack` methods
    // do not differ
  }

  @Test
  public void testCreateTwoTissuesForOneIdentityWithProject() throws Exception {
    // Goal: ensure one tissue associated with a predefined project and a single (but not pre-existing)
    // identity can be saved
    BulkSamplePage page = getCreatePage(2, projectId, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new LinkedHashMap<>();
    tissue.put(SamColumns.DESCRIPTION, "Description");
    tissue.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    tissue.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    tissue.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    tissue.put(SamColumns.RECEIPT_CONFIRMED, "True");
    tissue.put(SamColumns.RECEIPT_QC_PASSED, "True");
    tissue.put(SamColumns.RECEIPT_QC_NOTE, "");
    tissue.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(SamColumns.GROUP_ID, "1");
    tissue.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    tissue.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(SamColumns.PASSAGE_NUMBER, "");
    tissue.put(SamColumns.TIMES_RECEIVED, "1");
    // tube number will be added separately to differentiate the tissues
    tissue.put(SamColumns.SECONDARY_ID, "tube id 1");
    tissue.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    tissue.put(SamColumns.REGION, "Medulla oblongata");
    tissue.put(SamColumns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    tissue.forEach((k, v) -> table.enterText(k, 1, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(SamColumns.EXTERNAL_NAME, "ext3,ext4"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, tissue.get(SamColumns.EXTERNAL_NAME));
    table.enterText(SamColumns.EXTERNAL_NAME, 1, tissue.get(SamColumns.EXTERNAL_NAME));

    table.enterText(SamColumns.TUBE_NUMBER, 0, "1");
    table.enterText(SamColumns.TUBE_NUMBER, 1, "2");

    assertIdentityLookupWasSuccessful(table, 0);
    assertIdentityLookupWasSuccessful(table, 1);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long newId = getIdForRow(savedTable, 0);

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissue created = (SampleTissue) getSession().get(SampleTissueImpl.class, newId);

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    String row0Alias = savedTable.getText(SamColumns.ALIAS, 0);
    String row1Alias = savedTable.getText(SamColumns.ALIAS, 1);
    assertFalse("confirm alias generated", isStringEmptyOrNull(row0Alias));
    assertEquals("confirm same identity alias", row0Alias.substring(0, 9), row1Alias.substring(0, 9));
    // everything else should be the same as in testCreateOneTissueNoProject() since the `pack` methods
    // do not differ
  }

  @Test
  public void testCreateTissueProcessingSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(tissueProcessingDirectColumns);
    expectedHeadings.addAll(slideColumns);
    expectedHeadings.addAll(tissuePieceColumns);
    expectedHeadings.addAll(singleCellColumns);

    BulkSamplePage page = getCreatePage(1, null, SampleTissueProcessing.CATEGORY_NAME);
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testCreateTissueProcessingDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Set<String> sampleClasses = table.getDropdownOptions(SamColumns.SAMPLE_CLASS, 0);
    assertTrue(sampleClasses.size() > 1);
    assertTrue(sampleClasses.contains("Slide"));
    assertTrue(sampleClasses.contains("Tissue Piece"));

    table.enterText(SamColumns.SAMPLE_CLASS, 0, "Slide");
    Set<String> stains = table.getDropdownOptions(SamColumns.STAIN, 0);
    assertTrue(stains.size() > 1);
    assertTrue(stains.contains("Cresyl Violet"));

    table.enterText(SamColumns.SAMPLE_CLASS, 0, "Tissue Piece");
    Set<String> pieceTypes = table.getDropdownOptions(SamColumns.PIECE_TYPE, 0);
    assertTrue(pieceTypes.size() > 1);
    assertTrue(pieceTypes.contains("Curls"));

    table.enterText(SamColumns.SAMPLE_CLASS, 0, "Slide");
    table.enterText(SamColumns.STAIN, 0, "Cres");
    assertEquals("Cresyl Violet", table.getText(SamColumns.STAIN, 0));
  }

  @Test
  public void testCreateOneSlideNoProject() throws Exception {
    // Goal: ensure one slide can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> slide = new LinkedHashMap<>();
    slide.put(SamColumns.DESCRIPTION, "Description");
    slide.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    slide.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    slide.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    slide.put(SamColumns.RECEIPT_CONFIRMED, "True");
    slide.put(SamColumns.RECEIPT_QC_PASSED, "True");
    slide.put(SamColumns.RECEIPT_QC_NOTE, "");
    slide.put(SamColumns.PROJECT, "PONE");
    slide.put(SamColumns.ID_BARCODE, "103"); // increment
    slide.put(SamColumns.SAMPLE_CLASS, "Slide");
    slide.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    slide.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    slide.put(SamColumns.GROUP_ID, "1");
    slide.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    slide.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    slide.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    slide.put(SamColumns.PASSAGE_NUMBER, "");
    slide.put(SamColumns.TIMES_RECEIVED, "1");
    slide.put(SamColumns.TUBE_NUMBER, "1");
    slide.put(SamColumns.SECONDARY_ID, "tube id 1");
    slide.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    slide.put(SamColumns.REGION, "Medulla oblongata");
    slide.put(SamColumns.SLIDES, "7");
    slide.put(SamColumns.THICKNESS, "5");
    slide.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    slide.put(SamColumns.QC_STATUS, "Ready");

    slide.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    slide.put(SamColumns.EXTERNAL_NAME, "ext3"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, slide.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForSlide(slide, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneSlideWithProject() throws Exception {
    // Goal: ensure one slide with predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> slide = new LinkedHashMap<>();
    slide.put(SamColumns.DESCRIPTION, "Description");
    slide.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    slide.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    slide.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    slide.put(SamColumns.RECEIPT_CONFIRMED, "True");
    slide.put(SamColumns.RECEIPT_QC_PASSED, "True");
    slide.put(SamColumns.RECEIPT_QC_NOTE, "");
    slide.put(SamColumns.ID_BARCODE, "104"); // increment
    slide.put(SamColumns.SAMPLE_CLASS, "Slide");
    slide.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    slide.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    slide.put(SamColumns.GROUP_ID, "1");
    slide.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    slide.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    slide.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    slide.put(SamColumns.PASSAGE_NUMBER, "");
    slide.put(SamColumns.TIMES_RECEIVED, "1");
    slide.put(SamColumns.TUBE_NUMBER, "1");
    slide.put(SamColumns.SECONDARY_ID, "tube id 1");
    slide.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    slide.put(SamColumns.REGION, "Medulla oblongata");
    slide.put(SamColumns.SLIDES, "7");
    slide.put(SamColumns.THICKNESS, "5");
    slide.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    slide.put(SamColumns.QC_STATUS, "Ready");

    slide.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    slide.put(SamColumns.EXTERNAL_NAME, "ext4"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, slide.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long newId = getIdForRow(savedTable, 0);

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleSlide created = (SampleSlide) getSession().get(SampleSlideImpl.class, newId);

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // everything else should be the same as in testCreateOneSlideNoProject()
  }

  @Test
  public void testCreateOneCurlsNoProject() throws Exception {
    // Goal: ensure one Curls can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> curls = new LinkedHashMap<>();
    curls.put(SamColumns.DESCRIPTION, "Description");
    curls.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    curls.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    curls.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    curls.put(SamColumns.RECEIPT_CONFIRMED, "True");
    curls.put(SamColumns.RECEIPT_QC_PASSED, "True");
    curls.put(SamColumns.RECEIPT_QC_NOTE, "");
    curls.put(SamColumns.PROJECT, "PONE");
    curls.put(SamColumns.ID_BARCODE, "105"); // increment
    curls.put(SamColumns.SAMPLE_CLASS, "Tissue Piece");
    curls.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    curls.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    curls.put(SamColumns.EXTERNAL_NAME, "ext5"); // increment
    curls.put(SamColumns.GROUP_ID, "1");
    curls.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    curls.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    curls.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    curls.put(SamColumns.PASSAGE_NUMBER, "");
    curls.put(SamColumns.TIMES_RECEIVED, "1");
    curls.put(SamColumns.TUBE_NUMBER, "1");
    curls.put(SamColumns.SECONDARY_ID, "tube id 1");
    curls.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    curls.put(SamColumns.REGION, "Medulla oblongata");
    curls.put(SamColumns.QC_STATUS, "Ready");
    curls.put(SamColumns.PIECE_TYPE, "Curls");
    curls.put(SamColumns.SLIDES_CONSUMED, "2");

    curls.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    curls.put(SamColumns.EXTERNAL_NAME, "ext5"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, curls.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(curls, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneCurlsWithProject() throws Exception {
    // Goal: ensure one Curls associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> curls = new LinkedHashMap<>();
    curls.put(SamColumns.DESCRIPTION, "Description");
    curls.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    curls.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    curls.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    curls.put(SamColumns.RECEIPT_CONFIRMED, "True");
    curls.put(SamColumns.RECEIPT_QC_PASSED, "True");
    curls.put(SamColumns.RECEIPT_QC_NOTE, "");
    curls.put(SamColumns.ID_BARCODE, "106"); // increment
    curls.put(SamColumns.SAMPLE_CLASS, "Tissue Piece");
    curls.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    curls.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    curls.put(SamColumns.GROUP_ID, "1");
    curls.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    curls.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    curls.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    curls.put(SamColumns.PASSAGE_NUMBER, "");
    curls.put(SamColumns.TIMES_RECEIVED, "1");
    curls.put(SamColumns.TUBE_NUMBER, "1");
    curls.put(SamColumns.SECONDARY_ID, "tube id 1");
    curls.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    curls.put(SamColumns.REGION, "Medulla oblongata");
    curls.put(SamColumns.QC_STATUS, "Ready");
    curls.put(SamColumns.PIECE_TYPE, "Curls");
    curls.put(SamColumns.SLIDES_CONSUMED, "2");

    curls.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    curls.put(SamColumns.EXTERNAL_NAME, "ext6"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, curls.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long newId = getIdForRow(savedTable, 0);

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissueProcessing created = (SampleTissueProcessing) getSession().get(SampleTissueProcessingImpl.class, newId);

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // everything else should be the same as in testCreateOneCurlsNoProject
  }

  @Test
  public void testCreateOneSingleCell() throws Exception {
    // Goal: ensure one Single Cell can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> singleCell = new LinkedHashMap<>();
    singleCell.put(SamColumns.DESCRIPTION, "Description");
    singleCell.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    singleCell.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    singleCell.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    singleCell.put(SamColumns.RECEIPT_CONFIRMED, "True");
    singleCell.put(SamColumns.RECEIPT_QC_PASSED, "True");
    singleCell.put(SamColumns.RECEIPT_QC_NOTE, "");
    singleCell.put(SamColumns.PROJECT, "PONE");
    singleCell.put(SamColumns.ID_BARCODE, "105");
    singleCell.put(SamColumns.SAMPLE_CLASS, "Single Cell");
    singleCell.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    singleCell.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    singleCell.put(SamColumns.EXTERNAL_NAME, "ext5");
    singleCell.put(SamColumns.GROUP_ID, "1");
    singleCell.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    singleCell.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    singleCell.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    singleCell.put(SamColumns.PASSAGE_NUMBER, "");
    singleCell.put(SamColumns.TIMES_RECEIVED, "1");
    singleCell.put(SamColumns.TUBE_NUMBER, "1");
    singleCell.put(SamColumns.SECONDARY_ID, "tube id 1");
    singleCell.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    singleCell.put(SamColumns.REGION, "Medulla oblongata");
    singleCell.put(SamColumns.QC_STATUS, "Ready");
    singleCell.put(SamColumns.INITIAL_CELL_CONC, "12.34");
    singleCell.put(SamColumns.DIGESTION, "abcde");

    singleCell.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    singleCell.put(SamColumns.EXTERNAL_NAME, "ext5"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, singleCell.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForSingleCell(singleCell, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateStockSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(singleCellColumns);
    expectedHeadings.addAll(stockColumns);
    expectedHeadings.addAll(rnaStockColumns);
    expectedHeadings.addAll(singleCellStockColumns);

    BulkSamplePage page = getCreatePage(1, null, SampleStock.CATEGORY_NAME);
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testCreateStockDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Set<String> strStatuses = table.getDropdownOptions(SamColumns.STR_STATUS, 0);
    assertEquals(4, strStatuses.size());
    assertTrue(strStatuses.contains("Submitted"));
    assertTrue(strStatuses.contains("Fail"));

    table.enterText(SamColumns.STR_STATUS, 0, "Subm");
    assertEquals("Submitted", table.getText(SamColumns.STR_STATUS, 0));

    table.enterText(SamColumns.SAMPLE_CLASS, 0, "whole RNA (stock)");
    Set<String> dnaseTreated = table.getDropdownOptions(SamColumns.DNASE_TREATED, 0);
    assertEquals(2, dnaseTreated.size());
    assertTrue(dnaseTreated.contains("True"));
    assertTrue(dnaseTreated.contains("False"));
  }

  @Test
  public void testCreateOneGdnaStockNoProject() throws Exception {
    // Goal: ensure one gDNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaStock = new LinkedHashMap<>();
    gDnaStock.put(SamColumns.DESCRIPTION, "Description");
    gDnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaStock.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    gDnaStock.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    gDnaStock.put(SamColumns.RECEIPT_CONFIRMED, "True");
    gDnaStock.put(SamColumns.RECEIPT_QC_PASSED, "True");
    gDnaStock.put(SamColumns.RECEIPT_QC_NOTE, "");
    gDnaStock.put(SamColumns.PROJECT, "PONE");
    gDnaStock.put(SamColumns.ID_BARCODE, "107"); // increment
    gDnaStock.put(SamColumns.SAMPLE_CLASS, "gDNA (stock)");
    gDnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaStock.put(SamColumns.GROUP_ID, "1");
    gDnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaStock.put(SamColumns.TUBE_NUMBER, "1");
    gDnaStock.put(SamColumns.SECONDARY_ID, "tube id 1");
    gDnaStock.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    gDnaStock.put(SamColumns.REGION, "Medulla oblongata");
    gDnaStock.put(SamColumns.STR_STATUS, "Submitted");
    gDnaStock.put(SamColumns.VOLUME, "10.0");
    gDnaStock.put(SamColumns.CONCENTRATION, "3.75");
    gDnaStock.put(SamColumns.QC_STATUS, "Ready");

    gDnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaStock.put(SamColumns.EXTERNAL_NAME, "ext7"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, gDnaStock.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForStock(gDnaStock, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneGdnaStockWithProject() throws Exception {
    // Goal: ensure one gDNA (stock) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaStock = new LinkedHashMap<>();
    gDnaStock.put(SamColumns.DESCRIPTION, "Description");
    gDnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaStock.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    gDnaStock.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    gDnaStock.put(SamColumns.RECEIPT_CONFIRMED, "True");
    gDnaStock.put(SamColumns.RECEIPT_QC_PASSED, "True");
    gDnaStock.put(SamColumns.RECEIPT_QC_NOTE, "");
    gDnaStock.put(SamColumns.ID_BARCODE, "108"); // increment
    gDnaStock.put(SamColumns.SAMPLE_CLASS, "gDNA (stock)");
    gDnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaStock.put(SamColumns.GROUP_ID, "1");
    gDnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaStock.put(SamColumns.TUBE_NUMBER, "1");
    gDnaStock.put(SamColumns.SECONDARY_ID, "tube id 1");
    gDnaStock.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    gDnaStock.put(SamColumns.REGION, "Medulla oblongata");
    gDnaStock.put(SamColumns.STR_STATUS, "Submitted");
    gDnaStock.put(SamColumns.VOLUME, "10.0");
    gDnaStock.put(SamColumns.CONCENTRATION, "3.75");
    gDnaStock.put(SamColumns.QC_STATUS, "Ready");

    gDnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaStock.put(SamColumns.EXTERNAL_NAME, "ext8"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, gDnaStock.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleStock created =
        (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(getIdForRow(savedTable, 0)));

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // everything else should be the same as in testCreateOneGdnaStockNoProject
  }

  @Test
  public void testCreateOneSingleCellStock() throws Exception {
    // Goal: ensure one Single Cell DNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> stock = new LinkedHashMap<>();
    stock.put(SamColumns.DESCRIPTION, "Description");
    stock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    stock.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    stock.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    stock.put(SamColumns.RECEIPT_CONFIRMED, "True");
    stock.put(SamColumns.RECEIPT_QC_PASSED, "True");
    stock.put(SamColumns.RECEIPT_QC_NOTE, "");
    stock.put(SamColumns.PROJECT, "PONE");
    stock.put(SamColumns.ID_BARCODE, "107"); // increment
    stock.put(SamColumns.SAMPLE_CLASS, "Single Cell DNA (stock)");
    stock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    stock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    stock.put(SamColumns.GROUP_ID, "1");
    stock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    stock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    stock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    stock.put(SamColumns.PASSAGE_NUMBER, "");
    stock.put(SamColumns.TIMES_RECEIVED, "1");
    stock.put(SamColumns.TUBE_NUMBER, "1");
    stock.put(SamColumns.SECONDARY_ID, "tube id 1");
    stock.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    stock.put(SamColumns.REGION, "Medulla oblongata");
    stock.put(SamColumns.STR_STATUS, "Submitted");
    stock.put(SamColumns.VOLUME, "10.0");
    stock.put(SamColumns.CONCENTRATION, "3.75");
    stock.put(SamColumns.QC_STATUS, "Ready");
    stock.put(SamColumns.INITIAL_CELL_CONC, "12.34");
    stock.put(SamColumns.DIGESTION, "abcde");
    stock.put(SamColumns.TARGET_CELL_RECOVERY, "23");
    stock.put(SamColumns.CELL_VIABILITY, "34.56");
    stock.put(SamColumns.LOADING_CELL_CONC, "45.67");

    stock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    stock.put(SamColumns.EXTERNAL_NAME, "ext7"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, stock.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForSingleCellStock(stock, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneRnaStockNoProject() throws Exception {
    // Goal: ensure whole RNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaStock = new LinkedHashMap<>();
    rnaStock.put(SamColumns.DESCRIPTION, "Description");
    rnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaStock.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    rnaStock.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    rnaStock.put(SamColumns.RECEIPT_CONFIRMED, "True");
    rnaStock.put(SamColumns.RECEIPT_QC_PASSED, "True");
    rnaStock.put(SamColumns.RECEIPT_QC_NOTE, "");
    rnaStock.put(SamColumns.PROJECT, "PONE");
    rnaStock.put(SamColumns.ID_BARCODE, "109"); // increment
    rnaStock.put(SamColumns.SAMPLE_CLASS, "whole RNA (stock)");
    rnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaStock.put(SamColumns.GROUP_ID, "1");
    rnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    rnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    rnaStock.put(SamColumns.TUBE_NUMBER, "1");
    rnaStock.put(SamColumns.SECONDARY_ID, "tube id 1");
    rnaStock.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    rnaStock.put(SamColumns.REGION, "Medulla oblongata");
    rnaStock.put(SamColumns.STR_STATUS, "Submitted");
    rnaStock.put(SamColumns.DNASE_TREATED, "True");
    rnaStock.put(SamColumns.VOLUME, "10.0");
    rnaStock.put(SamColumns.CONCENTRATION, "3.75");
    rnaStock.put(SamColumns.QC_STATUS, "Ready");

    rnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaStock.put(SamColumns.EXTERNAL_NAME, "ext9"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, rnaStock.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForRnaStock(rnaStock, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneRnaStockWithProject() throws Exception {
    // Goal: ensure one whole RNA (stock) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaStock = new LinkedHashMap<>();
    rnaStock.put(SamColumns.DESCRIPTION, "Description");
    rnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaStock.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    rnaStock.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    rnaStock.put(SamColumns.RECEIPT_CONFIRMED, "True");
    rnaStock.put(SamColumns.RECEIPT_QC_PASSED, "True");
    rnaStock.put(SamColumns.RECEIPT_QC_NOTE, "");
    rnaStock.put(SamColumns.ID_BARCODE, "110"); // increment
    rnaStock.put(SamColumns.SAMPLE_CLASS, "whole RNA (stock)");
    rnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaStock.put(SamColumns.GROUP_ID, "1");
    rnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    rnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    rnaStock.put(SamColumns.TUBE_NUMBER, "1");
    rnaStock.put(SamColumns.SECONDARY_ID, "tube id 1");
    rnaStock.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    rnaStock.put(SamColumns.REGION, "Medulla oblongata");
    rnaStock.put(SamColumns.STR_STATUS, "Submitted");
    rnaStock.put(SamColumns.DNASE_TREATED, "True");
    rnaStock.put(SamColumns.VOLUME, "10.0");
    rnaStock.put(SamColumns.CONCENTRATION, "3.75");
    rnaStock.put(SamColumns.QC_STATUS, "Ready");

    rnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaStock.put(SamColumns.EXTERNAL_NAME, "ext10"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, rnaStock.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    Long newId = getIdForRow(savedTable, 0);

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, newId);

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // everything else should be the same as in testCreateOneRnaStockNoProject
  }

  @Test
  public void testCreateAliquotSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(singleCellColumns);
    expectedHeadings.addAll(stockColumns);
    expectedHeadings.addAll(rnaStockColumns);
    expectedHeadings.addAll(singleCellStockColumns);
    expectedHeadings.addAll(aliquotColumns);
    expectedHeadings.addAll(singleCellAliquotColumns);

    BulkSamplePage page = getCreatePage(1, null, SampleAliquot.CATEGORY_NAME);
    HandsontableUtils.testTableSetup(page, expectedHeadings, 1);
  }

  @Test
  public void testCreateAliquotDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Set<String> purposes = table.getDropdownOptions(SamColumns.PURPOSE, 0);
    assertEquals(11, purposes.size());
    assertTrue(purposes.contains("Library"));
    assertTrue(purposes.contains("Validation"));

    table.enterText(SamColumns.PURPOSE, 0, "Vali");
    assertEquals("Validation", table.getText(SamColumns.PURPOSE, 0));
  }

  @Test
  public void testCreateOneGdnaAliquotNoProject() throws Exception {
    // Goal: ensure one gDNA (aliquot) can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaAliquot = new LinkedHashMap<>();
    gDnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    gDnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaAliquot.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    gDnaAliquot.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    gDnaAliquot.put(SamColumns.RECEIPT_CONFIRMED, "True");
    gDnaAliquot.put(SamColumns.RECEIPT_QC_PASSED, "True");
    gDnaAliquot.put(SamColumns.RECEIPT_QC_NOTE, "");
    gDnaAliquot.put(SamColumns.PROJECT, "PONE");
    gDnaAliquot.put(SamColumns.ID_BARCODE, "111"); // increment
    gDnaAliquot.put(SamColumns.SAMPLE_CLASS, "gDNA (aliquot)");
    gDnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaAliquot.put(SamColumns.GROUP_ID, "1");
    gDnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    gDnaAliquot.put(SamColumns.SECONDARY_ID, "tube id 1");
    gDnaAliquot.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    gDnaAliquot.put(SamColumns.REGION, "Medulla oblongata");
    gDnaAliquot.put(SamColumns.STR_STATUS, "Submitted");
    gDnaAliquot.put(SamColumns.VOLUME, "10.0");
    gDnaAliquot.put(SamColumns.CONCENTRATION, "3.75");
    gDnaAliquot.put(SamColumns.QC_STATUS, "Ready");
    gDnaAliquot.put(SamColumns.PURPOSE, "Library");

    gDnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaAliquot.put(SamColumns.EXTERNAL_NAME, "ext11"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, gDnaAliquot.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForAliquot(gDnaAliquot, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneGdnaAliquotWithProject() throws Exception {
    // Goal: ensure one gDNA (aliquot) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaAliquot = new LinkedHashMap<>();
    gDnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    gDnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaAliquot.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    gDnaAliquot.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    gDnaAliquot.put(SamColumns.RECEIPT_CONFIRMED, "True");
    gDnaAliquot.put(SamColumns.RECEIPT_QC_PASSED, "True");
    gDnaAliquot.put(SamColumns.RECEIPT_QC_NOTE, "");
    gDnaAliquot.put(SamColumns.ID_BARCODE, "112"); // increment
    gDnaAliquot.put(SamColumns.SAMPLE_CLASS, "gDNA (aliquot)");
    gDnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaAliquot.put(SamColumns.GROUP_ID, "1");
    gDnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    gDnaAliquot.put(SamColumns.SECONDARY_ID, "tube id 1");
    gDnaAliquot.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    gDnaAliquot.put(SamColumns.REGION, "Medulla oblongata");
    gDnaAliquot.put(SamColumns.STR_STATUS, "Submitted");
    gDnaAliquot.put(SamColumns.VOLUME, "10.0");
    gDnaAliquot.put(SamColumns.CONCENTRATION, "3.75");
    gDnaAliquot.put(SamColumns.QC_STATUS, "Ready");
    gDnaAliquot.put(SamColumns.PURPOSE, "Library");

    gDnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaAliquot.put(SamColumns.EXTERNAL_NAME, "ext12"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, gDnaAliquot.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    Long newId = getIdForRow(savedTable, 0);

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleAliquot created = (SampleAliquot) getSession().get(SampleAliquotImpl.class, newId);

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // everything else should be the same as in testCreateOneGdnaAliquotNoProject
  }

  @Test
  public void testCreateOneSingleCellAliquotNoProject() throws Exception {
    // Goal: ensure one Single Cell DNA (aliquot) can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> aliquot = new LinkedHashMap<>();
    aliquot.put(SamColumns.DESCRIPTION, "Description");
    aliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    aliquot.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    aliquot.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    aliquot.put(SamColumns.RECEIPT_CONFIRMED, "True");
    aliquot.put(SamColumns.RECEIPT_QC_PASSED, "True");
    aliquot.put(SamColumns.RECEIPT_QC_NOTE, "");
    aliquot.put(SamColumns.PROJECT, "PONE");
    aliquot.put(SamColumns.ID_BARCODE, "111"); // increment
    aliquot.put(SamColumns.SAMPLE_CLASS, "Single Cell DNA (aliquot)");
    aliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    aliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    aliquot.put(SamColumns.GROUP_ID, "1");
    aliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    aliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    aliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    aliquot.put(SamColumns.PASSAGE_NUMBER, "");
    aliquot.put(SamColumns.TIMES_RECEIVED, "1");
    aliquot.put(SamColumns.TUBE_NUMBER, "1");
    aliquot.put(SamColumns.SECONDARY_ID, "tube id 1");
    aliquot.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    aliquot.put(SamColumns.REGION, "Medulla oblongata");
    aliquot.put(SamColumns.STR_STATUS, "Submitted");
    aliquot.put(SamColumns.VOLUME, "10.0");
    aliquot.put(SamColumns.CONCENTRATION, "3.75");
    aliquot.put(SamColumns.QC_STATUS, "Ready");
    aliquot.put(SamColumns.PURPOSE, "Library");
    aliquot.put(SamColumns.INITIAL_CELL_CONC, "12.34");
    aliquot.put(SamColumns.DIGESTION, "abcde");
    aliquot.put(SamColumns.TARGET_CELL_RECOVERY, "23");
    aliquot.put(SamColumns.CELL_VIABILITY, "34.56");
    aliquot.put(SamColumns.LOADING_CELL_CONC, "45.67");

    aliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    aliquot.put(SamColumns.EXTERNAL_NAME, "ext11"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, aliquot.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForSingleCellAliquot(aliquot, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneRnaAliquotNoProject() throws Exception {
    // Goal: ensure one whole RNA (aliquot) can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaAliquot = new LinkedHashMap<>();
    rnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    rnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaAliquot.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    rnaAliquot.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    rnaAliquot.put(SamColumns.RECEIPT_CONFIRMED, "True");
    rnaAliquot.put(SamColumns.RECEIPT_QC_PASSED, "True");
    rnaAliquot.put(SamColumns.RECEIPT_QC_NOTE, "");
    rnaAliquot.put(SamColumns.PROJECT, "PONE");
    rnaAliquot.put(SamColumns.ID_BARCODE, "113"); // increment
    rnaAliquot.put(SamColumns.SAMPLE_CLASS, "whole RNA (aliquot)");
    rnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaAliquot.put(SamColumns.GROUP_ID, "1");
    rnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    rnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    rnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    rnaAliquot.put(SamColumns.SECONDARY_ID, "tube id 1");
    rnaAliquot.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    rnaAliquot.put(SamColumns.REGION, "Medulla oblongata");
    rnaAliquot.put(SamColumns.STR_STATUS, "Submitted");
    rnaAliquot.put(SamColumns.DNASE_TREATED, "True");
    rnaAliquot.put(SamColumns.VOLUME, "10.0");
    rnaAliquot.put(SamColumns.CONCENTRATION, "3.75");
    rnaAliquot.put(SamColumns.QC_STATUS, "Ready");
    rnaAliquot.put(SamColumns.PURPOSE, "Validation");

    rnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaAliquot.put(SamColumns.EXTERNAL_NAME, "ext13"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, rnaAliquot.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForRnaAliquot(rnaAliquot, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneRnaAliquotWithProject() throws Exception {
    // Goal: ensure one whole RNA (aliquot) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaAliquot = new LinkedHashMap<>();
    rnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    rnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaAliquot.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    rnaAliquot.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    rnaAliquot.put(SamColumns.RECEIPT_CONFIRMED, "True");
    rnaAliquot.put(SamColumns.RECEIPT_QC_PASSED, "True");
    rnaAliquot.put(SamColumns.RECEIPT_QC_NOTE, "");
    rnaAliquot.put(SamColumns.ID_BARCODE, "114"); // increment
    rnaAliquot.put(SamColumns.SAMPLE_CLASS, "whole RNA (aliquot)");
    rnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaAliquot.put(SamColumns.GROUP_ID, "1");
    rnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    rnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    rnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    rnaAliquot.put(SamColumns.SECONDARY_ID, "tube id 1");
    rnaAliquot.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    rnaAliquot.put(SamColumns.REGION, "Medulla oblongata");
    rnaAliquot.put(SamColumns.STR_STATUS, "Submitted");
    rnaAliquot.put(SamColumns.DNASE_TREATED, "True");
    rnaAliquot.put(SamColumns.VOLUME, "10.0");
    rnaAliquot.put(SamColumns.CONCENTRATION, "3.75");
    rnaAliquot.put(SamColumns.QC_STATUS, "Ready");
    rnaAliquot.put(SamColumns.PURPOSE, "Library");

    rnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaAliquot.put(SamColumns.EXTERNAL_NAME, "ext14"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, rnaAliquot.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForRnaAliquot(rnaAliquot, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateIdentitySetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, SampleIdentity.CATEGORY_NAME);
    HandsontableUtils.testTableSetup(page, identityColumns, 1);
  }

  @Test
  public void testCreateIdentityDropdowns() throws Exception {
    // Goal: ensure dropdowns are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, SampleIdentity.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Set<String> sampleTypes = table.getDropdownOptions(SamColumns.SAMPLE_TYPE, 0);
    assertEquals(8, sampleTypes.size());
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(SamColumns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(SamColumns.SAMPLE_TYPE, 0));

    Set<String> projects = table.getDropdownOptions(SamColumns.PROJECT, 0);
    assertTrue(projects.size() > 0);
    assertTrue(projects.contains("PONE"));

    table.enterText(SamColumns.PROJECT, 0, "PONE");
    assertEquals("PONE", table.getText(SamColumns.PROJECT, 0));

    Set<String> donorSexes = table.getDropdownOptions(SamColumns.DONOR_SEX, 0);
    assertEquals(5, donorSexes.size());
    assertTrue(donorSexes.contains("Female"));
    assertTrue(donorSexes.contains("Unspecified"));

    table.enterText(SamColumns.DONOR_SEX, 0, "Unspe");
    assertEquals("Unspecified", table.getText(SamColumns.DONOR_SEX, 0));

    Set<String> qcStatuses = table.getDropdownOptions(SamColumns.QC_STATUS, 0);
    assertEquals(10, qcStatuses.size());
    assertTrue(qcStatuses.contains("Ready"));
    assertTrue(qcStatuses.contains("Refused Consent"));

    table.enterText(SamColumns.QC_STATUS, 0, "Rea");
    assertEquals("Ready", table.getText(SamColumns.QC_STATUS, 0));
  }

  @Test
  public void testCreateOneIdentityNoProject() throws Exception {
    // Goal: ensure one identity can be saved
    BulkSamplePage page = getCreatePage(1, null, SampleIdentity.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> identity = new LinkedHashMap<>();
    identity.put(SamColumns.ALIAS, "PTWO_1001");
    identity.put(SamColumns.DESCRIPTION, "");
    identity.put(SamColumns.PROJECT, "PTWO"); // different project so as not to mess with the SampleNumberPerProject
                                              // generator
    identity.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    identity.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    identity.put(SamColumns.EXTERNAL_NAME, "ext2001"); // increment
    identity.put(SamColumns.DONOR_SEX, "Female");
    identity.put(SamColumns.CONSENT, ConsentLevel.ALL_PROJECTS.getLabel());
    identity.put(SamColumns.GROUP_ID, "");
    identity.put(SamColumns.GROUP_DESCRIPTION, "");
    identity.put(SamColumns.QC_STATUS, "Ready");
    identity.put(SamColumns.CREATION_DATE, "2018-07-17");

    identity.forEach((k, v) -> table.enterText(k, 0, v));

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    // verify attributes against what got saved to the database
    assertAllForIdentity(identity, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testCreateOneIdentityWithProject() throws Exception {
    // Goal: ensure one identity associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, 2L, SampleIdentity.CATEGORY_NAME);
    // different project so as not to mess with the SampleNumberPerProject generator
    HandsOnTable table = page.getTable();

    Map<String, String> identity = new LinkedHashMap<>();
    identity.put(SamColumns.ALIAS, "PTWO_1002");
    identity.put(SamColumns.DESCRIPTION, "");
    identity.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    identity.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    identity.put(SamColumns.EXTERNAL_NAME, "ext2002"); // increment
    identity.put(SamColumns.GROUP_ID, "");
    identity.put(SamColumns.GROUP_DESCRIPTION, "");
    identity.put(SamColumns.QC_STATUS, "Ready");
    identity.put(SamColumns.CREATION_DATE, "");

    identity.forEach((k, v) -> table.enterText(k, 0, v));

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    Long newId = getIdForRow(savedTable, 0);

    // verify attributes on the Edit single Sample page
    Project predefined = (Project) getSession().get(ProjectImpl.class, 2L);
    SampleIdentity created = (SampleIdentity) getSession().get(SampleIdentityImpl.class, newId);

    assertEquals("confirm project", predefined.getCode(), created.getProject().getCode());
    // rest should be same as testCreateOneIdentityNoProject
  }

  private void assertIdentityLookupWasSuccessful(HandsOnTable table, int rowNum) {
    table.waitForSearch(SamColumns.IDENTITY_ALIAS, rowNum);
    assertEquals("identity lookup was successful", "First Receipt (PONE)", table.getText(SamColumns.IDENTITY_ALIAS, 0));
  }

  @Test
  public void testLookupExistingIdentity() throws Exception {
    BulkSamplePage page = getCreatePage(1, null, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();
    table.enterText(SamColumns.PROJECT, 0, "TEST");
    table.enterText(SamColumns.EXTERNAL_NAME, 0, "TEST_external_1");
    table.waitForSearch(SamColumns.IDENTITY_ALIAS, 0);
    assertEquals("TEST_0001 -- TEST_external_1", table.getText(SamColumns.IDENTITY_ALIAS, 0));
  }

}
