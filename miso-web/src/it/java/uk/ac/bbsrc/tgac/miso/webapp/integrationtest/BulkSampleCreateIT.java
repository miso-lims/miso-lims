package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
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
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class BulkSampleCreateIT extends AbstractBulkSampleIT {

  // columns for creating Identity and everything else
  private static final Set<String> identityColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.PROJECT, SamColumns.EXTERNAL_NAME, SamColumns.DONOR_SEX,
      SamColumns.CONSENT, SamColumns.SUBPROJECT, SamColumns.SAMPLE_CLASS, SamColumns.GROUP_ID, SamColumns.GROUP_DESCRIPTION,
      SamColumns.CREATION_DATE, SamColumns.QC_STATUS, SamColumns.QC_NOTE);

  // columns for creating Tissue and everything downstream of it
  private static final Set<String> tissueColumns = Sets.newHashSet(SamColumns.ID_BARCODE, SamColumns.BOX_SEARCH, SamColumns.BOX_ALIAS,
      SamColumns.BOX_POSITION, SamColumns.DISCARDED, SamColumns.RECEIVE_DATE, SamColumns.IDENTITY_ALIAS, SamColumns.TISSUE_ORIGIN,
      SamColumns.TISSUE_TYPE, SamColumns.PASSAGE_NUMBER, SamColumns.TIMES_RECEIVED, SamColumns.TUBE_NUMBER, SamColumns.LAB,
      SamColumns.SECONDARY_ID, SamColumns.TISSUE_MATERIAL, SamColumns.REGION);

  // columns specific to creating Slides
  private static final Set<String> slideColumns = Sets.newHashSet(SamColumns.SLIDES, SamColumns.DISCARDS, SamColumns.THICKNESS,
      SamColumns.STAIN);

  // columns specific to creating curls
  private static final Set<String> curlsColumns = Sets.newHashSet();

  // columns specific to creating gDNA stocks
  private static final Set<String> gDnaStockColumns = Sets.newHashSet(SamColumns.STR_STATUS, SamColumns.VOLUME, SamColumns.VOLUME_UNITS,
      SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS);

  // columns specific to creating RNA stocks
  private static final Set<String> rnaStockColumns = Sets.newHashSet(SamColumns.STR_STATUS, SamColumns.VOLUME, SamColumns.VOLUME_UNITS,
      SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS, SamColumns.DNASE_TREATED);

  // columns specific to creating aliquots
  private static final Set<String> aliquotColumns = Sets.newHashSet(SamColumns.PURPOSE);

  @Before
  public void setup() {
    loginAdmin();
  }

  private BulkSamplePage getCreatePage(Integer quantity, Long projectId, Long sampleClassId) {
    return BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), quantity, projectId, sampleClassId);
  }

  @Test
  public void testSaveEmptyFail() throws Exception {
    // Goal: ensure save fails and error message is shown when trying to save with required fields empty
    BulkSamplePage page = getCreatePage(1, projectId, rAliquotClassId);
    HandsOnTable table = page.getTable();
    HandsOnTableSaveResult result = table.save();
    assertEquals(0, result.getItemsSaved());
    assertFalse(result.getSaveErrors().isEmpty());
  }

  @Test
  public void testCreateTissueSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);

    BulkSamplePage page = getCreatePage(1, null, tissueClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateTissueDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, tissueClassId);
    HandsOnTable table = page.getTable();

    Set<String> sampleTypes = table.getDropdownOptions(SamColumns.SAMPLE_TYPE, 0);
    assertTrue(sampleTypes.size() >= 8);
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(SamColumns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(SamColumns.SAMPLE_TYPE, 0));

    Set<String> projects = table.getDropdownOptions(SamColumns.PROJECT, 0);
    assertTrue(projects.size() > 0);
    assertTrue(projects.contains("PRO1"));

    table.enterText(SamColumns.PROJECT, 0, "PRO1");
    assertEquals("PRO1", table.getText(SamColumns.PROJECT, 0));

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

    Set<String> labs = table.getDropdownOptions(SamColumns.LAB, 0);
    assertTrue(labs.size() >= 3); // 2 + (None)
    assertTrue(labs.contains("Pathology (University Health Network)"));
    assertTrue(labs.contains("(None)"));

    table.enterText(SamColumns.LAB, 0, "Bio");
    assertEquals("BioBank (University Health Network)", table.getText(SamColumns.LAB, 0));

    Set<String> materials = table.getDropdownOptions(SamColumns.TISSUE_MATERIAL, 0);
    assertTrue(materials.size() >= 4); // 3 + (None)
    assertTrue(materials.contains("FFPE"));
    assertTrue(materials.contains("(None)"));

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
    // Goal: ensure that changing the external name value causes the identity alias dropdown to be populated
    BulkSamplePage page = getCreatePage(1, null, 23L);
    HandsOnTable table = page.getTable();

    assertTrue("identity alias is empty", isStringEmptyOrNull(table.getText(SamColumns.IDENTITY_ALIAS, 0)));
    table.enterText(SamColumns.IDENTITY_ALIAS, 0, "Identity 1");
    table.waitForSearch(SamColumns.IDENTITY_ALIAS, 0);
    assertTrue("identity alias no longer empty", !isStringEmptyOrNull(table.getText(SamColumns.IDENTITY_ALIAS, 0)));
  }

  @Test
  public void testCreateOneTissueNoProject() throws Exception {
    // Goal: ensure one tissue can be saved
    BulkSamplePage page = getCreatePage(1, null, tissueClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new HashMap<>();
    tissue.put(SamColumns.DESCRIPTION, "Description");
    tissue.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    tissue.put(SamColumns.ID_BARCODE, "101"); // increment
    tissue.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(SamColumns.PROJECT, "PRO1");
    tissue.put(SamColumns.GROUP_ID, "1");
    tissue.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    tissue.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(SamColumns.PASSAGE_NUMBER, "");
    tissue.put(SamColumns.TIMES_RECEIVED, "1");
    tissue.put(SamColumns.TUBE_NUMBER, "1");
    tissue.put(SamColumns.LAB, "BioBank (University Health Network)");
    tissue.put(SamColumns.SECONDARY_ID, "tube id 1");
    tissue.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    tissue.put(SamColumns.REGION, "Medulla oblongata");
    tissue.put(SamColumns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(SamColumns.EXTERNAL_NAME, "ext1"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, tissue.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    saveSingleAndAssertSuccess(table);

    tissue.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    tissue.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissue(tissue, getIdForRow(table, 0), true);
  }

  @Test
  public void testCreateOneTissueWithProject() throws Exception {
    // Goal: ensure one tissue associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, tissueClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new HashMap<>();
    tissue.put(SamColumns.DESCRIPTION, "Description");
    tissue.put(SamColumns.RECEIVE_DATE, "2017-07-17");
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
    tissue.put(SamColumns.LAB, "BioBank (University Health Network)");
    tissue.put(SamColumns.SECONDARY_ID, "tube id 1");
    tissue.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    tissue.put(SamColumns.REGION, "Medulla oblongata");
    tissue.put(SamColumns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(SamColumns.EXTERNAL_NAME, "ext2"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, tissue.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    saveSingleAndAssertSuccess(table);

    tissue.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissue created = (SampleTissue) getSession().get(SampleTissueImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneTissueNoProject() since the `pack` methods do not differ
  }

  @Test
  public void testCreateTwoTissuesForOneIdentityWithProject() throws Exception {
    // Goal: ensure one tissue associated with a predefined project and a single (but not pre-existing) identity can be saved
    BulkSamplePage page = getCreatePage(2, projectId, tissueClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new HashMap<>();
    tissue.put(SamColumns.DESCRIPTION, "Description");
    tissue.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    tissue.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(SamColumns.GROUP_ID, "1");
    tissue.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    tissue.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(SamColumns.PASSAGE_NUMBER, "");
    tissue.put(SamColumns.TIMES_RECEIVED, "1");
    // tube number will be added separately to differentiate the tissues
    tissue.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSeveralAndAssertAllSuccess(table, 2);

    tissue.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissue created = (SampleTissue) getSession().get(SampleTissueImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    String row0Alias = table.getText(SamColumns.ALIAS, 0);
    String row1Alias = table.getText(SamColumns.ALIAS, 1);
    assertFalse("confirm alias generated", isStringEmptyOrNull(row0Alias));
    assertEquals("confirm same identity alias", row0Alias.substring(0, 9), row1Alias.substring(0, 9));
    // everything else should be the same as in testCreateOneTissueNoProject() since the `pack` methods do not differ
  }

  @Test
  public void testCreateSlideSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(slideColumns);

    BulkSamplePage page = getCreatePage(1, null, slideClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateSlideDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, slideClassId);
    HandsOnTable table = page.getTable();

    Set<String> stains = table.getDropdownOptions(SamColumns.STAIN, 0);
    assertEquals(3, stains.size()); // 2 + (None)
    assertTrue(stains.contains("Cresyl Violet"));
    assertTrue(stains.contains("(None)"));

    table.enterText(SamColumns.STAIN, 0, "Cres");
    assertEquals("Cresyl Violet", table.getText(SamColumns.STAIN, 0));
  }

  @Test
  public void testCreateOneSlideNoProject() throws Exception {
    // Goal: ensure one slide can be saved
    BulkSamplePage page = getCreatePage(1, null, slideClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> slide = new HashMap<>();
    slide.put(SamColumns.DESCRIPTION, "Description");
    slide.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    slide.put(SamColumns.ID_BARCODE, "103"); // increment
    slide.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    slide.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    slide.put(SamColumns.PROJECT, "PRO1");
    slide.put(SamColumns.GROUP_ID, "1");
    slide.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    slide.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    slide.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    slide.put(SamColumns.PASSAGE_NUMBER, "");
    slide.put(SamColumns.TIMES_RECEIVED, "1");
    slide.put(SamColumns.TUBE_NUMBER, "1");
    slide.put(SamColumns.LAB, "BioBank (University Health Network)");
    slide.put(SamColumns.SECONDARY_ID, "tube id 1");
    slide.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    slide.put(SamColumns.REGION, "Medulla oblongata");
    slide.put(SamColumns.SLIDES, "7");
    slide.put(SamColumns.DISCARDS, "0");
    slide.put(SamColumns.THICKNESS, "5");
    slide.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    slide.put(SamColumns.QC_STATUS, "Ready");

    slide.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    slide.put(SamColumns.EXTERNAL_NAME, "ext3"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, slide.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    saveSingleAndAssertSuccess(table);

    slide.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    slide.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    assertAllForSlide(slide, getIdForRow(table, 0), true);
  }

  @Test
  public void testCreateOneSlideWithProject() throws Exception {
    // Goal: ensure one slide with predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, slideClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> slide = new HashMap<>();
    slide.put(SamColumns.DESCRIPTION, "Description");
    slide.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    slide.put(SamColumns.ID_BARCODE, "104"); // increment
    slide.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    slide.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    slide.put(SamColumns.GROUP_ID, "1");
    slide.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    slide.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    slide.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    slide.put(SamColumns.PASSAGE_NUMBER, "");
    slide.put(SamColumns.TIMES_RECEIVED, "1");
    slide.put(SamColumns.TUBE_NUMBER, "1");
    slide.put(SamColumns.LAB, "BioBank (University Health Network)");
    slide.put(SamColumns.SECONDARY_ID, "tube id 1");
    slide.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    slide.put(SamColumns.REGION, "Medulla oblongata");
    slide.put(SamColumns.SLIDES, "7");
    slide.put(SamColumns.DISCARDS, "0");
    slide.put(SamColumns.THICKNESS, "5");
    slide.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    slide.put(SamColumns.QC_STATUS, "Ready");

    slide.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    slide.put(SamColumns.EXTERNAL_NAME, "ext4"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, slide.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    saveSingleAndAssertSuccess(table);

    slide.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleSlide created = (SampleSlide) getSession().get(SampleSlideImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneSlideNoProject()
  }

  @Test
  public void testCreateCurlsSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(curlsColumns);

    BulkSamplePage page = getCreatePage(1, null, curlsClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateOneCurlsNoProject() throws Exception {
    // Goal: ensure one Curls can be saved
    BulkSamplePage page = getCreatePage(1, null, curlsClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> curls = new HashMap<>();
    curls.put(SamColumns.DESCRIPTION, "Description");
    curls.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    curls.put(SamColumns.ID_BARCODE, "105"); // increment
    curls.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    curls.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    curls.put(SamColumns.PROJECT, "PRO1");
    curls.put(SamColumns.EXTERNAL_NAME, "ext5"); // increment
    curls.put(SamColumns.GROUP_ID, "1");
    curls.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    curls.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    curls.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    curls.put(SamColumns.PASSAGE_NUMBER, "");
    curls.put(SamColumns.TIMES_RECEIVED, "1");
    curls.put(SamColumns.TUBE_NUMBER, "1");
    curls.put(SamColumns.LAB, "BioBank (University Health Network)");
    curls.put(SamColumns.SECONDARY_ID, "tube id 1");
    curls.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    curls.put(SamColumns.REGION, "Medulla oblongata");
    curls.put(SamColumns.QC_STATUS, "Ready");

    curls.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    curls.put(SamColumns.EXTERNAL_NAME, "ext5"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, curls.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    saveSingleAndAssertSuccess(table);

    curls.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    curls.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(curls, getIdForRow(table, 0), true);
  }

  @Test
  public void testCreateOneCurlsWithProject() throws Exception {
    // Goal: ensure one Curls associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, curlsClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> curls = new HashMap<>();
    curls.put(SamColumns.DESCRIPTION, "Description");
    curls.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    curls.put(SamColumns.ID_BARCODE, "106"); // increment
    curls.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    curls.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    curls.put(SamColumns.GROUP_ID, "1");
    curls.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    curls.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    curls.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    curls.put(SamColumns.PASSAGE_NUMBER, "");
    curls.put(SamColumns.TIMES_RECEIVED, "1");
    curls.put(SamColumns.TUBE_NUMBER, "1");
    curls.put(SamColumns.LAB, "BioBank (University Health Network)");
    curls.put(SamColumns.SECONDARY_ID, "tube id 1");
    curls.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    curls.put(SamColumns.REGION, "Medulla oblongata");
    curls.put(SamColumns.QC_STATUS, "Ready");

    curls.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    curls.put(SamColumns.EXTERNAL_NAME, "ext6"); // increment
    table.enterText(SamColumns.EXTERNAL_NAME, 0, curls.get(SamColumns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(table, 0);

    saveSingleAndAssertSuccess(table);

    curls.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissueProcessing created = (SampleTissueProcessing) getSession().get(SampleTissueProcessingImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneCurlsNoProject
  }

  @Test
  public void testCreateGdnaStockSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(gDnaStockColumns);

    BulkSamplePage page = getCreatePage(1, null, gStockClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateGdnaStockDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, gStockClassId);
    HandsOnTable table = page.getTable();

    Set<String> strStatuses = table.getDropdownOptions(SamColumns.STR_STATUS, 0);
    assertEquals(4, strStatuses.size());
    assertTrue(strStatuses.contains("Submitted"));
    assertTrue(strStatuses.contains("Fail"));

    table.enterText(SamColumns.STR_STATUS, 0, "Subm");
    assertEquals("Submitted", table.getText(SamColumns.STR_STATUS, 0));
  }

  @Test
  public void testCreateOneGdnaStockNoProject() throws Exception {
    // Goal: ensure one gDNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, gStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaStock = new HashMap<>();
    gDnaStock.put(SamColumns.DESCRIPTION, "Description");
    gDnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaStock.put(SamColumns.ID_BARCODE, "107"); // increment
    gDnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaStock.put(SamColumns.PROJECT, "PRO1");
    gDnaStock.put(SamColumns.GROUP_ID, "1");
    gDnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaStock.put(SamColumns.TUBE_NUMBER, "1");
    gDnaStock.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    gDnaStock.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    gDnaStock.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    assertAllForStock(gDnaStock, getIdForRow(table, 0), true, false);
  }

  @Test
  public void testCreateOneGdnaStockWithProject() throws Exception {
    // Goal: ensure one gDNA (stock) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, gStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaStock = new HashMap<>();
    gDnaStock.put(SamColumns.DESCRIPTION, "Description");
    gDnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaStock.put(SamColumns.ID_BARCODE, "108"); // increment
    gDnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaStock.put(SamColumns.GROUP_ID, "1");
    gDnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaStock.put(SamColumns.TUBE_NUMBER, "1");
    gDnaStock.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    gDnaStock.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    gDnaStock.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(getIdForRow(table, 0)));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneGdnaStockNoProject
  }

  @Test
  public void testCreateRnaStockSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(rnaStockColumns);

    BulkSamplePage page = getCreatePage(1, null, rStockClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateRnaStockDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, rStockClassId);
    HandsOnTable table = page.getTable();

    Set<String> dnaseTreated = table.getDropdownOptions(SamColumns.DNASE_TREATED, 0);
    assertEquals(2, dnaseTreated.size());
    assertTrue(dnaseTreated.contains("True"));
    assertTrue(dnaseTreated.contains("False"));
  }

  @Test
  public void testCreateOneRnaStockNoProject() throws Exception {
    // Goal: ensure whole RNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, rStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaStock = new HashMap<>();
    rnaStock.put(SamColumns.DESCRIPTION, "Description");
    rnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaStock.put(SamColumns.ID_BARCODE, "109"); // increment
    rnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaStock.put(SamColumns.PROJECT, "PRO1");
    rnaStock.put(SamColumns.GROUP_ID, "1");
    rnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    rnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    rnaStock.put(SamColumns.TUBE_NUMBER, "1");
    rnaStock.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    rnaStock.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    rnaStock.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, getIdForRow(table, 0));

    assertAllForStock(rnaStock, getIdForRow(table, 0), true, true);
    assertRnaStockSampleAttributes(rnaStock, created);
  }

  @Test
  public void testCreateOneRnaStockWithProject() throws Exception {
    // Goal: ensure one whole RNA (stock) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, rStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaStock = new HashMap<>();
    rnaStock.put(SamColumns.DESCRIPTION, "Description");
    rnaStock.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaStock.put(SamColumns.ID_BARCODE, "110"); // increment
    rnaStock.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaStock.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaStock.put(SamColumns.GROUP_ID, "1");
    rnaStock.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaStock.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaStock.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaStock.put(SamColumns.PASSAGE_NUMBER, "");
    rnaStock.put(SamColumns.TIMES_RECEIVED, "1");
    rnaStock.put(SamColumns.TUBE_NUMBER, "1");
    rnaStock.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    rnaStock.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    rnaStock.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneRnaStockNoProject
  }

  @Test
  public void testCreateGdnaAliquotSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(gDnaStockColumns);
    expectedHeadings.addAll(aliquotColumns);

    BulkSamplePage page = getCreatePage(1, null, gAliquotClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateGdnaAliquotDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, gAliquotClassId);
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
    BulkSamplePage page = getCreatePage(1, null, gAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaAliquot = new HashMap<>();
    gDnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    gDnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaAliquot.put(SamColumns.ID_BARCODE, "111"); // increment
    gDnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaAliquot.put(SamColumns.PROJECT, "PRO1");
    gDnaAliquot.put(SamColumns.GROUP_ID, "1");
    gDnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    gDnaAliquot.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    gDnaAliquot.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    gDnaAliquot.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    assertAllForAliquot(gDnaAliquot, getIdForRow(table, 0), true, false);

  }

  @Test
  public void testCreateOneGdnaAliquotWithProject() throws Exception {
    // Goal: ensure one gDNA (aliquot) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, gAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaAliquot = new HashMap<>();
    gDnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    gDnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    gDnaAliquot.put(SamColumns.ID_BARCODE, "112"); // increment
    gDnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    gDnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaAliquot.put(SamColumns.GROUP_ID, "1");
    gDnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    gDnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    gDnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    gDnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    gDnaAliquot.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    gDnaAliquot.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    gDnaAliquot.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleAliquot created = (SampleAliquot) getSession().get(SampleAliquotImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneGdnaAliquotNoProject
  }

  @Test
  public void testCreateRnaAliquotSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(identityColumns);
    expectedHeadings.addAll(tissueColumns);
    expectedHeadings.addAll(rnaStockColumns);
    expectedHeadings.addAll(aliquotColumns);

    BulkSamplePage page = getCreatePage(1, null, rAliquotClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateOneRnaAliquotNoProject() throws Exception {
    // Goal: ensure one whole RNA (aliquot) can be saved
    BulkSamplePage page = getCreatePage(1, null, rAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaAliquot = new HashMap<>();
    rnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    rnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaAliquot.put(SamColumns.ID_BARCODE, "113"); // increment
    rnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaAliquot.put(SamColumns.PROJECT, "PRO1");
    rnaAliquot.put(SamColumns.GROUP_ID, "1");
    rnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    rnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    rnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    rnaAliquot.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    rnaAliquot.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    rnaAliquot.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    assertAllForAliquot(rnaAliquot, getIdForRow(table, 0), true, true);
  }

  @Test
  public void testCreateOneRnaAliquotWithProject() throws Exception {
    // Goal: ensure one whole RNA (aliquot) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, rAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaAliquot = new HashMap<>();
    rnaAliquot.put(SamColumns.DESCRIPTION, "Description");
    rnaAliquot.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    rnaAliquot.put(SamColumns.ID_BARCODE, "114"); // increment
    rnaAliquot.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    rnaAliquot.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaAliquot.put(SamColumns.GROUP_ID, "1");
    rnaAliquot.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    rnaAliquot.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaAliquot.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    rnaAliquot.put(SamColumns.PASSAGE_NUMBER, "");
    rnaAliquot.put(SamColumns.TIMES_RECEIVED, "1");
    rnaAliquot.put(SamColumns.TUBE_NUMBER, "1");
    rnaAliquot.put(SamColumns.LAB, "BioBank (University Health Network)");
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

    saveSingleAndAssertSuccess(table);

    rnaAliquot.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    rnaAliquot.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleAliquot created = (SampleAliquot) getSession().get(SampleAliquotImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneRnaAliquotNoProject
  }

  @Test
  public void testCreateIdentitySetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, identityClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(identityColumns.size(), headings.size());
    for (String col : identityColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateIdentityDropdowns() throws Exception {
    // Goal: ensure dropdowns are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, identityClassId);
    HandsOnTable table = page.getTable();

    Set<String> sampleTypes = table.getDropdownOptions(SamColumns.SAMPLE_TYPE, 0);
    assertEquals(8, sampleTypes.size());
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(SamColumns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(SamColumns.SAMPLE_TYPE, 0));

    Set<String> projects = table.getDropdownOptions(SamColumns.PROJECT, 0);
    assertTrue(projects.size() > 0);
    assertTrue(projects.contains("PRO1"));

    table.enterText(SamColumns.PROJECT, 0, "PRO1");
    assertEquals("PRO1", table.getText(SamColumns.PROJECT, 0));

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
  public void testCreateIdentityDependencyCells() throws Exception {
    // Goal: ensure that cells which depend on other columns are updated once the other columns are updated
    BulkSamplePage page = getCreatePage(1, null, identityClassId);
    HandsOnTable table = page.getTable();

    assertFalse(table.isWritable(SamColumns.QC_NOTE, 0));

    table.enterText(SamColumns.QC_STATUS, 0, "Okd by Collaborator");
    assertTrue(table.isWritable(SamColumns.QC_NOTE, 0));
    table.enterText(SamColumns.QC_NOTE, 0, "writable note");
    assertEquals("note is writable", "writable note", table.getText(SamColumns.QC_NOTE, 0));
  }

  // TODO: fix and re-enable (Sometimes selects project PRO1 instead of PRO2 - may be interference from other tests)
  @Ignore
  @Test
  public void testCreateOneIdentityNoProject() throws Exception {
    // Goal: ensure one identity can be saved
    BulkSamplePage page = getCreatePage(1, null, identityClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> identity = new HashMap<>();
    identity.put(SamColumns.ALIAS, "PRO2_1001");
    identity.put(SamColumns.DESCRIPTION, "");
    identity.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    identity.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    identity.put(SamColumns.PROJECT, "PRO2"); // different project so as not to mess with the SampleNumberPerProject generator
    identity.put(SamColumns.EXTERNAL_NAME, "ext2001"); // increment
    identity.put(SamColumns.DONOR_SEX, "Female");
    identity.put(SamColumns.DONOR_SEX, ConsentLevel.ALL_PROJECTS.getLabel());
    identity.put(SamColumns.GROUP_ID, "");
    identity.put(SamColumns.GROUP_DESCRIPTION, "");
    identity.put(SamColumns.QC_STATUS, "Ready");
    identity.put(SamColumns.CREATION_DATE, "2018-07-17");

    identity.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    identity.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));

    // verify attributes against what got saved to the database
    assertAllForIdentity(identity, getIdForRow(table, 0), true);
  }

  @Test
  public void testCreateOneIdentityWithProject() throws Exception {
    // Goal: ensure one identity associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, 2L, identityClassId);
    // different project so as not to mess with the SampleNumberPerProject generator
    HandsOnTable table = page.getTable();

    Map<String, String> identity = new HashMap<>();
    identity.put(SamColumns.ALIAS, "PRO2_1002");
    identity.put(SamColumns.DESCRIPTION, "");
    identity.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    identity.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    identity.put(SamColumns.EXTERNAL_NAME, "ext2002"); // increment
    identity.put(SamColumns.GROUP_ID, "");
    identity.put(SamColumns.GROUP_DESCRIPTION, "");
    identity.put(SamColumns.QC_STATUS, "Ready");
    identity.put(SamColumns.CREATION_DATE, "");

    identity.forEach((k, v) -> table.enterText(k, 0, v));

    saveSingleAndAssertSuccess(table);

    identity.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    String newId = table.getText(SamColumns.NAME, 0).substring(3, table.getText(SamColumns.NAME, 0).length());

    // verify attributes on the Edit single Sample page
    Project predefined = (Project) getSession().get(ProjectImpl.class, 2L);
    SampleIdentity created = (SampleIdentity) getSession().get(SampleIdentityImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // rest should be same as testCreateOneIdentityNoProject
  }

  private void assertIdentityLookupWasSuccessful(HandsOnTable table, int rowNum) {
    table.waitForSearch(SamColumns.IDENTITY_ALIAS, rowNum);
    assertEquals("identity lookup was successful", "First Receipt (PRO1)", table.getText(SamColumns.IDENTITY_ALIAS, 0));
  }

}
