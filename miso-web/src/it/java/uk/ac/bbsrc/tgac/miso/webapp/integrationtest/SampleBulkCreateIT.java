package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
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
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class SampleBulkCreateIT extends AbstractIT {

  private static final Set<String> identityColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID, Columns.GROUP_DESCRIPTION,
      Columns.QC_STATUS, Columns.QC_NOTE);

  private static final Set<String> tissueColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.QC_STATUS,
      Columns.QC_NOTE);

  private static final Set<String> slideColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.SLIDES,
      Columns.DISCARDS, Columns.THICKNESS, Columns.STAIN, Columns.QC_STATUS, Columns.QC_NOTE);

  private static final Set<String> curlsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.QC_STATUS,
      Columns.QC_NOTE);

  private static final Set<String> gDnaStockColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.STR_STATUS,
      Columns.VOLUME, Columns.CONCENTRATION, Columns.QC_STATUS, Columns.QC_NOTE);

  private static final Set<String> rnaStockColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.STR_STATUS,
      Columns.VOLUME, Columns.CONCENTRATION, Columns.DNASE_TREATED, Columns.NEW_RIN, Columns.NEW_DV200, Columns.QC_STATUS,
      Columns.QC_NOTE);

  private static final Set<String> gDnaAliquotColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.STR_STATUS,
      Columns.VOLUME, Columns.CONCENTRATION, Columns.QC_STATUS, Columns.QC_NOTE, Columns.PURPOSE);

  private static final Set<String> rnaAliquotColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE, Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT,
      Columns.EXTERNAL_NAME, Columns.IDENTITY_ALIAS, Columns.DONOR_SEX, Columns.SAMPLE_CLASS, Columns.GROUP_ID,
      Columns.GROUP_DESCRIPTION, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.PASSAGE_NUMBER, Columns.TIMES_RECEIVED,
      Columns.TUBE_NUMBER, Columns.LAB, Columns.EXT_INST_ID, Columns.TISSUE_MATERIAL, Columns.REGION, Columns.STR_STATUS,
      Columns.VOLUME, Columns.CONCENTRATION, Columns.DNASE_TREATED, Columns.NEW_RIN, Columns.NEW_DV200, Columns.QC_STATUS,
      Columns.QC_NOTE, Columns.PURPOSE);

  private static final long projectId = 1L;
  private static final long identityClassId = 1L;
  private static final long tissueClassId = 23L;
  private static final long slideClassId = 24L;
  private static final long curlsClassId = 8L;
  private static final long gStockClassId = 11L;
  private static final long rStockClassId = 13L;
  private static final long gAliquotClassId = 15L;
  private static final Long rAliquotClassId = 17L;

  @Before
  public void setup() {
    loginAdmin();
  }

  private BulkSamplePage getCreatePage(Integer quantity, Long projectId, Long sampleClassId) {
    return BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), quantity, projectId, sampleClassId);
  }

  @Test
  public void testCreateTissueSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, tissueClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(tissueColumns.size(), headings.size());
    for (String col : tissueColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateTissueDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, tissueClassId);
    HandsOnTable table = page.getTable();

    List<String> sampleTypes = table.getDropdownOptions(Columns.SAMPLE_TYPE, 0);
    assertEquals(8, sampleTypes.size());
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(Columns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(Columns.SAMPLE_TYPE, 0));

    List<String> projects = table.getDropdownOptions(Columns.PROJECT, 0);
    assertTrue(projects.size() > 0);
    assertTrue(projects.contains("PRO1"));

    table.enterText(Columns.PROJECT, 0, "PRO1");
    assertEquals("PRO1", table.getText(Columns.PROJECT, 0));

    List<String> donorSexes = table.getDropdownOptions(Columns.DONOR_SEX, 0);
    assertEquals(5, donorSexes.size());
    assertTrue(donorSexes.contains("Female"));
    assertTrue(donorSexes.contains("Unspecified"));

    table.enterText(Columns.DONOR_SEX, 0, "Unspe");
    assertEquals("Unspecified", table.getText(Columns.DONOR_SEX, 0));

    List<String> tissueOrigins = table.getDropdownOptions(Columns.TISSUE_ORIGIN, 0);
    assertEquals(3, tissueOrigins.size());
    assertTrue(tissueOrigins.contains("Bn (Brain)"));
    assertTrue(tissueOrigins.contains("Pa (Pancreas)"));

    table.enterText(Columns.TISSUE_ORIGIN, 0, "L");
    assertEquals("Ly (Lymphocyte)", table.getText(Columns.TISSUE_ORIGIN, 0));

    List<String> tissueTypes = table.getDropdownOptions(Columns.TISSUE_TYPE, 0);
    assertEquals(14, tissueTypes.size());
    assertTrue(tissueTypes.contains("P (Primary tumour)"));
    assertTrue(tissueTypes.contains("n (Unknown)"));

    table.enterText(Columns.TISSUE_TYPE, 0, "Benign");
    assertEquals("B (Benign tumour)", table.getText(Columns.TISSUE_TYPE, 0));

    List<String> labs = table.getDropdownOptions(Columns.LAB, 0);
    assertEquals(3, labs.size()); // 2 + (None)
    assertTrue(labs.contains("Pathology (University Health Network)"));
    assertTrue(labs.contains("(None)"));

    table.enterText(Columns.LAB, 0, "Bio");
    assertEquals("BioBank (University Health Network)", table.getText(Columns.LAB, 0));

    List<String> materials = table.getDropdownOptions(Columns.TISSUE_MATERIAL, 0);
    assertEquals(4, materials.size()); // 3 + (None)
    assertTrue(materials.contains("FFPE"));
    assertTrue(materials.contains("(None)"));

    table.enterText(Columns.TISSUE_MATERIAL, 0, "Fresh");
    assertEquals("Fresh Frozen", table.getText(Columns.TISSUE_MATERIAL, 0));

    List<String> qcStatuses = table.getDropdownOptions(Columns.QC_STATUS, 0);
    assertEquals(10, qcStatuses.size());
    assertTrue(qcStatuses.contains("Ready"));
    assertTrue(qcStatuses.contains("Refused Consent"));

    table.enterText(Columns.QC_STATUS, 0, "Rea");
    assertEquals("Ready", table.getText(Columns.QC_STATUS, 0));
  }

  @Test
  public void testCreateTissueDependencyCells() throws Exception {
    // Goal: ensure that changing the external name value causes the identity alias dropdown to be populated
    BulkSamplePage page = getCreatePage(1, null, 23L);
    HandsOnTable table = page.getTable();

    assertTrue("identity alias is empty", isStringEmptyOrNull(table.getText(Columns.IDENTITY_ALIAS, 0)));
    table.enterText(Columns.IDENTITY_ALIAS, 0, "Identity 1");
    page.waitForIdentityLookup();
    assertTrue("identity alias no longer empty", !isStringEmptyOrNull(table.getText(Columns.IDENTITY_ALIAS, 0)));
  }

  @Test
  public void testCreateOneTissueNoProject() throws Exception {
    // Goal: ensure one tissue can be saved
    BulkSamplePage page = getCreatePage(1, null, tissueClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new HashMap<>();
    tissue.put(Columns.DESCRIPTION, "Description");
    tissue.put(Columns.RECEIVE_DATE, "2017-07-17");
    tissue.put(Columns.ID_BARCODE, "101"); // increment
    tissue.put(Columns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(Columns.PROJECT, "PRO1");
    tissue.put(Columns.GROUP_ID, "1");
    tissue.put(Columns.GROUP_DESCRIPTION, "Test one");
    tissue.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(Columns.TIMES_RECEIVED, "1");
    tissue.put(Columns.TUBE_NUMBER, "1");
    tissue.put(Columns.LAB, "BioBank (University Health Network)");
    tissue.put(Columns.EXT_INST_ID, "tube id 1");
    tissue.put(Columns.TISSUE_MATERIAL, "FFPE");
    tissue.put(Columns.REGION, "Medulla oblongata");
    tissue.put(Columns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(Columns.EXTERNAL_NAME, "ext1"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, tissue.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    tissue.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    tissue.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleTissue created = (SampleTissue) getSession().get(SampleTissueImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(tissue, created);
    assertDetailedSampleAttributes(tissue, created);
    assertSampleClass("Tissue", created);
    assertTissueAttributes(tissue, created);
  }

  @Test
  public void testCreateOneTissueWithProject() throws Exception {
    // Goal: ensure one tissue associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, tissueClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> tissue = new HashMap<>();
    tissue.put(Columns.DESCRIPTION, "Description");
    tissue.put(Columns.RECEIVE_DATE, "2017-07-17");
    tissue.put(Columns.ID_BARCODE, "102"); // increment
    tissue.put(Columns.SAMPLE_TYPE, "GENOMIC");
    tissue.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    tissue.put(Columns.GROUP_ID, "1");
    tissue.put(Columns.GROUP_DESCRIPTION, "Test one");
    tissue.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    tissue.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    tissue.put(Columns.TIMES_RECEIVED, "1");
    tissue.put(Columns.TUBE_NUMBER, "1");
    tissue.put(Columns.LAB, "BioBank (University Health Network)");
    tissue.put(Columns.EXT_INST_ID, "tube id 1");
    tissue.put(Columns.TISSUE_MATERIAL, "FFPE");
    tissue.put(Columns.REGION, "Medulla oblongata");
    tissue.put(Columns.QC_STATUS, "Ready");

    tissue.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    tissue.put(Columns.EXTERNAL_NAME, "ext2"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, tissue.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    tissue.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissue created = (SampleTissue) getSession().get(SampleTissueImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneTissueNoProject() since the `pack` methods do not differ
  }

  @Test
  public void testCreateSlideSetup() throws Exception {
    // Goal: ensure all expected fields are present, and no extra
    BulkSamplePage page = getCreatePage(1, null, slideClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(slideColumns.size(), headings.size());
    for (String col : slideColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateSlideDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, slideClassId);
    HandsOnTable table = page.getTable();

    List<String> stains = table.getDropdownOptions(Columns.STAIN, 0);
    assertEquals(3, stains.size()); // 2 + (None)
    assertTrue(stains.contains("Cresyl Violet"));
    assertTrue(stains.contains("(None)"));

    table.enterText(Columns.STAIN, 0, "Cres");
    assertEquals("Cresyl Violet", table.getText(Columns.STAIN, 0));
  }

  @Test
  public void testCreateSlideDepencencyCells() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateOneSlideNoProject() throws Exception {
    // Goal: ensure one slide can be saved
    BulkSamplePage page = getCreatePage(1, null, slideClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> slide = new HashMap<>();
    slide.put(Columns.DESCRIPTION, "Description");
    slide.put(Columns.RECEIVE_DATE, "2017-07-17");
    slide.put(Columns.ID_BARCODE, "103"); // increment
    slide.put(Columns.SAMPLE_TYPE, "GENOMIC");
    slide.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    slide.put(Columns.PROJECT, "PRO1");
    slide.put(Columns.GROUP_ID, "1");
    slide.put(Columns.GROUP_DESCRIPTION, "Test one");
    slide.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    slide.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    slide.put(Columns.TIMES_RECEIVED, "1");
    slide.put(Columns.TUBE_NUMBER, "1");
    slide.put(Columns.LAB, "BioBank (University Health Network)");
    slide.put(Columns.EXT_INST_ID, "tube id 1");
    slide.put(Columns.TISSUE_MATERIAL, "FFPE");
    slide.put(Columns.REGION, "Medulla oblongata");
    slide.put(Columns.SLIDES, "7");
    slide.put(Columns.DISCARDS, "0");
    slide.put(Columns.THICKNESS, "5");
    slide.put(Columns.STAIN, "Hematoxylin+Eosin");
    slide.put(Columns.QC_STATUS, "Ready");

    slide.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    slide.put(Columns.EXTERNAL_NAME, "ext3"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, slide.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    slide.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    slide.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleSlide created = (SampleSlide) getSession().get(SampleSlideImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(slide, created);
    assertDetailedSampleAttributes(slide, created);
    assertSampleClass("Slide", created);
    assertSlideSampleAttributes(slide, created);

    SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, created);
    assertTissueAttributes(slide, tissueParent);
  }

  @Test
  public void testCreateOneSlideWithProject() throws Exception {
    // Goal: ensure one slide with predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, slideClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> slide = new HashMap<>();
    slide.put(Columns.DESCRIPTION, "Description");
    slide.put(Columns.RECEIVE_DATE, "2017-07-17");
    slide.put(Columns.ID_BARCODE, "104"); // increment
    slide.put(Columns.SAMPLE_TYPE, "GENOMIC");
    slide.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    slide.put(Columns.GROUP_ID, "1");
    slide.put(Columns.GROUP_DESCRIPTION, "Test one");
    slide.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    slide.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    slide.put(Columns.TIMES_RECEIVED, "1");
    slide.put(Columns.TUBE_NUMBER, "1");
    slide.put(Columns.LAB, "BioBank (University Health Network)");
    slide.put(Columns.EXT_INST_ID, "tube id 1");
    slide.put(Columns.TISSUE_MATERIAL, "FFPE");
    slide.put(Columns.REGION, "Medulla oblongata");
    slide.put(Columns.SLIDES, "7");
    slide.put(Columns.DISCARDS, "0");
    slide.put(Columns.THICKNESS, "5");
    slide.put(Columns.STAIN, "Hematoxylin+Eosin");
    slide.put(Columns.QC_STATUS, "Ready");

    slide.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    slide.put(Columns.EXTERNAL_NAME, "ext4"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, slide.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    slide.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleSlide created = (SampleSlide) getSession().get(SampleSlideImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneSlideNoProject()
  }

  @Test
  public void testCreateCurlsSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, curlsClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(curlsColumns.size(), headings.size());
    for (String col : curlsColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateCurlsDropdowns() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateCurlsDependencyCells() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateOneCurlsNoProject() throws Exception {
    // Goal: ensure one Curls can be saved
    BulkSamplePage page = getCreatePage(1, null, curlsClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> curls = new HashMap<>();
    curls.put(Columns.DESCRIPTION, "Description");
    curls.put(Columns.RECEIVE_DATE, "2017-07-17");
    curls.put(Columns.ID_BARCODE, "105"); // increment
    curls.put(Columns.SAMPLE_TYPE, "GENOMIC");
    curls.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    curls.put(Columns.PROJECT, "PRO1");
    curls.put(Columns.EXTERNAL_NAME, "ext5"); // increment
    curls.put(Columns.GROUP_ID, "1");
    curls.put(Columns.GROUP_DESCRIPTION, "Test one");
    curls.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    curls.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    curls.put(Columns.TIMES_RECEIVED, "1");
    curls.put(Columns.TUBE_NUMBER, "1");
    curls.put(Columns.LAB, "BioBank (University Health Network)");
    curls.put(Columns.EXT_INST_ID, "tube id 1");
    curls.put(Columns.TISSUE_MATERIAL, "FFPE");
    curls.put(Columns.REGION, "Medulla oblongata");
    curls.put(Columns.QC_STATUS, "Ready");

    curls.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    curls.put(Columns.EXTERNAL_NAME, "ext5"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, curls.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    curls.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    curls.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleTissueProcessing created = (SampleTissueProcessing) getSession().get(SampleTissueProcessingImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(curls, created);
    assertDetailedSampleAttributes(curls, created);
    assertSampleClass("Curls", created);

    SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, created);
    assertTissueAttributes(curls, tissueParent);
  }

  @Test
  public void testCreateOneCurlsWithProject() throws Exception {
    // Goal: ensure one Curls associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, curlsClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> curls = new HashMap<>();
    curls.put(Columns.DESCRIPTION, "Description");
    curls.put(Columns.RECEIVE_DATE, "2017-07-17");
    curls.put(Columns.ID_BARCODE, "106"); // increment
    curls.put(Columns.SAMPLE_TYPE, "GENOMIC");
    curls.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    curls.put(Columns.GROUP_ID, "1");
    curls.put(Columns.GROUP_DESCRIPTION, "Test one");
    curls.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    curls.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    curls.put(Columns.TIMES_RECEIVED, "1");
    curls.put(Columns.TUBE_NUMBER, "1");
    curls.put(Columns.LAB, "BioBank (University Health Network)");
    curls.put(Columns.EXT_INST_ID, "tube id 1");
    curls.put(Columns.TISSUE_MATERIAL, "FFPE");
    curls.put(Columns.REGION, "Medulla oblongata");
    curls.put(Columns.QC_STATUS, "Ready");

    curls.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    curls.put(Columns.EXTERNAL_NAME, "ext6"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, curls.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    curls.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleTissueProcessing created = (SampleTissueProcessing) getSession().get(SampleTissueProcessingImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneCurlsNoProject
  }

  @Test
  public void testCreateGdnaStockSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, gStockClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(gDnaStockColumns.size(), headings.size());
    for (String col : gDnaStockColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateGdnaStockDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, gStockClassId);
    HandsOnTable table = page.getTable();

    List<String> strStatuses = table.getDropdownOptions(Columns.STR_STATUS, 0);
    assertEquals(4, strStatuses.size());
    assertTrue(strStatuses.contains("Submitted"));
    assertTrue(strStatuses.contains("Fail"));

    table.enterText(Columns.STR_STATUS, 0, "Subm");
    assertEquals("Submitted", table.getText(Columns.STR_STATUS, 0));
  }

  @Test
  public void testCreateGdnaStockDependencyCells() {
    // none unique to this class at this time
  }

  @Test
  public void testCreateOneGdnaStockNoProject() throws Exception {
    // Goal: ensure one gDNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, gStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaStock = new HashMap<>();
    gDnaStock.put(Columns.DESCRIPTION, "Description");
    gDnaStock.put(Columns.RECEIVE_DATE, "2017-07-17");
    gDnaStock.put(Columns.ID_BARCODE, "107"); // increment
    gDnaStock.put(Columns.SAMPLE_TYPE, "GENOMIC");
    gDnaStock.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaStock.put(Columns.PROJECT, "PRO1");
    gDnaStock.put(Columns.GROUP_ID, "1");
    gDnaStock.put(Columns.GROUP_DESCRIPTION, "Test one");
    gDnaStock.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaStock.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaStock.put(Columns.TIMES_RECEIVED, "1");
    gDnaStock.put(Columns.TUBE_NUMBER, "1");
    gDnaStock.put(Columns.LAB, "BioBank (University Health Network)");
    gDnaStock.put(Columns.EXT_INST_ID, "tube id 1");
    gDnaStock.put(Columns.TISSUE_MATERIAL, "FFPE");
    gDnaStock.put(Columns.REGION, "Medulla oblongata");
    gDnaStock.put(Columns.STR_STATUS, "Submitted");
    gDnaStock.put(Columns.VOLUME, "10.0");
    gDnaStock.put(Columns.CONCENTRATION, "3.75");
    gDnaStock.put(Columns.QC_STATUS, "Ready");

    gDnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaStock.put(Columns.EXTERNAL_NAME, "ext7"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, gDnaStock.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    gDnaStock.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    gDnaStock.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(gDnaStock, created);
    assertDetailedSampleAttributes(gDnaStock, created);
    assertSampleClass("gDNA (stock)", created);
    assertStockAttributes(gDnaStock, created);
    assertAnalyteAttributes(gDnaStock, created);

    SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, created);
    assertTissueAttributes(gDnaStock, tissueParent);
  }

  @Test
  public void testCreateOneGdnaStockWithProject() throws Exception {
    // Goal: ensure one gDNA (stock) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, gStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaStock = new HashMap<>();
    gDnaStock.put(Columns.DESCRIPTION, "Description");
    gDnaStock.put(Columns.RECEIVE_DATE, "2017-07-17");
    gDnaStock.put(Columns.ID_BARCODE, "108"); // increment
    gDnaStock.put(Columns.SAMPLE_TYPE, "GENOMIC");
    gDnaStock.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaStock.put(Columns.GROUP_ID, "1");
    gDnaStock.put(Columns.GROUP_DESCRIPTION, "Test one");
    gDnaStock.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaStock.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaStock.put(Columns.TIMES_RECEIVED, "1");
    gDnaStock.put(Columns.TUBE_NUMBER, "1");
    gDnaStock.put(Columns.LAB, "BioBank (University Health Network)");
    gDnaStock.put(Columns.EXT_INST_ID, "tube id 1");
    gDnaStock.put(Columns.TISSUE_MATERIAL, "FFPE");
    gDnaStock.put(Columns.REGION, "Medulla oblongata");
    gDnaStock.put(Columns.STR_STATUS, "Submitted");
    gDnaStock.put(Columns.VOLUME, "10.0");
    gDnaStock.put(Columns.CONCENTRATION, "3.75");
    gDnaStock.put(Columns.QC_STATUS, "Ready");

    gDnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaStock.put(Columns.EXTERNAL_NAME, "ext8"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, gDnaStock.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    gDnaStock.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    gDnaStock.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneGdnaStockNoProject
  }

  @Test
  public void testCreateRnaStockSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, rStockClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(rnaStockColumns.size(), headings.size());
    for (String col : rnaStockColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateRnaStockDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, rStockClassId);
    HandsOnTable table = page.getTable();

    List<String> dnaseTreated = table.getDropdownOptions(Columns.DNASE_TREATED, 0);
    assertEquals(2, dnaseTreated.size());
    assertTrue(dnaseTreated.contains("True"));
    assertTrue(dnaseTreated.contains("False"));
  }

  @Test
  public void testCreateRnaStockDependencyCells() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateOneRnaStockNoProject() throws Exception {
    // Goal: ensure whole RNA (stock) can be saved
    BulkSamplePage page = getCreatePage(1, null, rStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaStock = new HashMap<>();
    rnaStock.put(Columns.DESCRIPTION, "Description");
    rnaStock.put(Columns.RECEIVE_DATE, "2017-07-17");
    rnaStock.put(Columns.ID_BARCODE, "109"); // increment
    rnaStock.put(Columns.SAMPLE_TYPE, "GENOMIC");
    rnaStock.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaStock.put(Columns.PROJECT, "PRO1");
    rnaStock.put(Columns.GROUP_ID, "1");
    rnaStock.put(Columns.GROUP_DESCRIPTION, "Test one");
    rnaStock.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaStock.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    rnaStock.put(Columns.TIMES_RECEIVED, "1");
    rnaStock.put(Columns.TUBE_NUMBER, "1");
    rnaStock.put(Columns.LAB, "BioBank (University Health Network)");
    rnaStock.put(Columns.EXT_INST_ID, "tube id 1");
    rnaStock.put(Columns.TISSUE_MATERIAL, "FFPE");
    rnaStock.put(Columns.REGION, "Medulla oblongata");
    rnaStock.put(Columns.STR_STATUS, "Submitted");
    rnaStock.put(Columns.DNASE_TREATED, "True");
    rnaStock.put(Columns.VOLUME, "10.0");
    rnaStock.put(Columns.CONCENTRATION, "3.75");
    rnaStock.put(Columns.NEW_RIN, "2.7");
    rnaStock.put(Columns.NEW_DV200, "92.55");
    rnaStock.put(Columns.QC_STATUS, "Ready");

    rnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaStock.put(Columns.EXTERNAL_NAME, "ext9"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, rnaStock.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    rnaStock.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    rnaStock.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(rnaStock, created);
    assertDetailedSampleAttributes(rnaStock, created);
    assertSampleClass("whole RNA (stock)", created);
    assertStockAttributes(rnaStock, created);
    assertAnalyteAttributes(rnaStock, created);
    assertRnaSampleAttributes(rnaStock, created);

    SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, created);
    assertTissueAttributes(rnaStock, tissueParent);

    // verify QCs
    Collection<SampleQC> sampleQcs = created.getSampleQCs();
    assertEquals(2, sampleQcs.size());
    for (SampleQC qc : sampleQcs) {
      switch (qc.getQcType().getName()) {
      case "RIN":
        assertEquals(rnaStock.get(Columns.NEW_RIN).toString(), qc.getResults().toString());
        break;
      case "DV200":
        assertEquals(rnaStock.get(Columns.NEW_DV200).toString(), qc.getResults().toString());
        break;
      default:
        throw new IllegalArgumentException("Found unexpected QC of type " + qc.getQcType().getName());
      }
    }
  }

  @Test
  public void testCreateOneRnaStockWithProject() throws Exception {
    // Goal: ensure one whole RNA (stock) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, rStockClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaStock = new HashMap<>();
    rnaStock.put(Columns.DESCRIPTION, "Description");
    rnaStock.put(Columns.RECEIVE_DATE, "2017-07-17");
    rnaStock.put(Columns.ID_BARCODE, "110"); // increment
    rnaStock.put(Columns.SAMPLE_TYPE, "GENOMIC");
    rnaStock.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaStock.put(Columns.GROUP_ID, "1");
    rnaStock.put(Columns.GROUP_DESCRIPTION, "Test one");
    rnaStock.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaStock.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    rnaStock.put(Columns.TIMES_RECEIVED, "1");
    rnaStock.put(Columns.TUBE_NUMBER, "1");
    rnaStock.put(Columns.LAB, "BioBank (University Health Network)");
    rnaStock.put(Columns.EXT_INST_ID, "tube id 1");
    rnaStock.put(Columns.TISSUE_MATERIAL, "FFPE");
    rnaStock.put(Columns.REGION, "Medulla oblongata");
    rnaStock.put(Columns.STR_STATUS, "Submitted");
    rnaStock.put(Columns.DNASE_TREATED, "True");
    rnaStock.put(Columns.VOLUME, "10.0");
    rnaStock.put(Columns.CONCENTRATION, "3.75");
    rnaStock.put(Columns.NEW_RIN, "2.7");
    rnaStock.put(Columns.QC_STATUS, "Ready");

    rnaStock.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaStock.put(Columns.EXTERNAL_NAME, "ext10"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, rnaStock.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    rnaStock.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    rnaStock.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleStock created = (SampleStock) getSession().get(SampleStockImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneRnaStockNoProject
  }

  @Test
  public void testCreateGdnaAliquotSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, gAliquotClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(gDnaAliquotColumns.size(), headings.size());
    for (String col : gDnaAliquotColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateGdnaAliquotDropdowns() throws Exception {
    // Goal: ensure dropdowns unique to this class are created correctly and values can be selected
    BulkSamplePage page = getCreatePage(1, null, gAliquotClassId);
    HandsOnTable table = page.getTable();

    List<String> purposes = table.getDropdownOptions(Columns.PURPOSE, 0);
    assertEquals(11, purposes.size());
    assertTrue(purposes.contains("Library"));
    assertTrue(purposes.contains("Validation"));

    table.enterText(Columns.PURPOSE, 0, "Vali");
    assertEquals("Validation", table.getText(Columns.PURPOSE, 0));
  }

  @Test
  public void testCreateGdnaAliquotDependencyCells() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateOneGdnaAliquotNoProject() throws Exception {
    // Goal: ensure one gDNA (aliquot) can be saved
    BulkSamplePage page = getCreatePage(1, null, gAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaAliquot = new HashMap<>();
    gDnaAliquot.put(Columns.DESCRIPTION, "Description");
    gDnaAliquot.put(Columns.RECEIVE_DATE, "2017-07-17");
    gDnaAliquot.put(Columns.ID_BARCODE, "111"); // increment
    gDnaAliquot.put(Columns.SAMPLE_TYPE, "GENOMIC");
    gDnaAliquot.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaAliquot.put(Columns.PROJECT, "PRO1");
    gDnaAliquot.put(Columns.GROUP_ID, "1");
    gDnaAliquot.put(Columns.GROUP_DESCRIPTION, "Test one");
    gDnaAliquot.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaAliquot.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaAliquot.put(Columns.TIMES_RECEIVED, "1");
    gDnaAliquot.put(Columns.TUBE_NUMBER, "1");
    gDnaAliquot.put(Columns.LAB, "BioBank (University Health Network)");
    gDnaAliquot.put(Columns.EXT_INST_ID, "tube id 1");
    gDnaAliquot.put(Columns.TISSUE_MATERIAL, "FFPE");
    gDnaAliquot.put(Columns.REGION, "Medulla oblongata");
    gDnaAliquot.put(Columns.STR_STATUS, "Submitted");
    gDnaAliquot.put(Columns.VOLUME, "10.0");
    gDnaAliquot.put(Columns.CONCENTRATION, "3.75");
    gDnaAliquot.put(Columns.QC_STATUS, "Ready");
    gDnaAliquot.put(Columns.PURPOSE, "Library");

    gDnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaAliquot.put(Columns.EXTERNAL_NAME, "ext11"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, gDnaAliquot.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    gDnaAliquot.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    gDnaAliquot.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleAliquot created = (SampleAliquot) getSession().get(SampleAliquotImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(gDnaAliquot, created);
    assertDetailedSampleAttributes(gDnaAliquot, created);
    assertSampleClass("gDNA (aliquot)", created);
    assertAnalyteAttributes(gDnaAliquot, created);
    assertAliquotAttributes(gDnaAliquot, created);

    SampleStock stockParent = LimsUtils.getParent(SampleStock.class, created);
    assertStockAttributes(gDnaAliquot, stockParent);

    SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, created);
    assertTissueAttributes(gDnaAliquot, tissueParent);
  }

  @Test
  public void testCreateOneGdnaAliquotWithProject() throws Exception {
    // Goal: ensure one gDNA (aliquot) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, gAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> gDnaAliquot = new HashMap<>();
    gDnaAliquot.put(Columns.DESCRIPTION, "Description");
    gDnaAliquot.put(Columns.RECEIVE_DATE, "2017-07-17");
    gDnaAliquot.put(Columns.ID_BARCODE, "112"); // increment
    gDnaAliquot.put(Columns.SAMPLE_TYPE, "GENOMIC");
    gDnaAliquot.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    gDnaAliquot.put(Columns.GROUP_ID, "1");
    gDnaAliquot.put(Columns.GROUP_DESCRIPTION, "Test one");
    gDnaAliquot.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    gDnaAliquot.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    gDnaAliquot.put(Columns.TIMES_RECEIVED, "1");
    gDnaAliquot.put(Columns.TUBE_NUMBER, "1");
    gDnaAliquot.put(Columns.LAB, "BioBank (University Health Network)");
    gDnaAliquot.put(Columns.EXT_INST_ID, "tube id 1");
    gDnaAliquot.put(Columns.TISSUE_MATERIAL, "FFPE");
    gDnaAliquot.put(Columns.REGION, "Medulla oblongata");
    gDnaAliquot.put(Columns.STR_STATUS, "Submitted");
    gDnaAliquot.put(Columns.VOLUME, "10.0");
    gDnaAliquot.put(Columns.CONCENTRATION, "3.75");
    gDnaAliquot.put(Columns.QC_STATUS, "Ready");
    gDnaAliquot.put(Columns.PURPOSE, "Library");

    gDnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    gDnaAliquot.put(Columns.EXTERNAL_NAME, "ext12"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, gDnaAliquot.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    gDnaAliquot.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    gDnaAliquot.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleAliquot created = (SampleAliquot) getSession().get(SampleAliquotImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be the same as in testCreateOneGdnaAliquotNoProject
  }

  @Test
  public void testCreateRnaAliquotSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, rAliquotClassId);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(rnaAliquotColumns.size(), headings.size());
    for (String col : rnaAliquotColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testCreateRnaAliquotDropdowns() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateRnaAliquotDependencyCells() throws Exception {
    // none unique to this class at this time
  }

  @Test
  public void testCreateOneRnaAliquotNoProject() throws Exception {
    // Goal: ensure one whole RNA (aliquot) can be saved
    BulkSamplePage page = getCreatePage(1, null, rAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaAliquot = new HashMap<>();
    rnaAliquot.put(Columns.DESCRIPTION, "Description");
    rnaAliquot.put(Columns.RECEIVE_DATE, "2017-07-17");
    rnaAliquot.put(Columns.ID_BARCODE, "113"); // increment
    rnaAliquot.put(Columns.SAMPLE_TYPE, "GENOMIC");
    rnaAliquot.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaAliquot.put(Columns.PROJECT, "PRO1");
    rnaAliquot.put(Columns.GROUP_ID, "1");
    rnaAliquot.put(Columns.GROUP_DESCRIPTION, "Test one");
    rnaAliquot.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaAliquot.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    rnaAliquot.put(Columns.TIMES_RECEIVED, "1");
    rnaAliquot.put(Columns.TUBE_NUMBER, "1");
    rnaAliquot.put(Columns.LAB, "BioBank (University Health Network)");
    rnaAliquot.put(Columns.EXT_INST_ID, "tube id 1");
    rnaAliquot.put(Columns.TISSUE_MATERIAL, "FFPE");
    rnaAliquot.put(Columns.REGION, "Medulla oblongata");
    rnaAliquot.put(Columns.STR_STATUS, "Submitted");
    rnaAliquot.put(Columns.DNASE_TREATED, "True");
    rnaAliquot.put(Columns.VOLUME, "10.0");
    rnaAliquot.put(Columns.CONCENTRATION, "3.75");
    rnaAliquot.put(Columns.QC_STATUS, "Ready");
    rnaAliquot.put(Columns.NEW_RIN, "2.7");
    rnaAliquot.put(Columns.NEW_DV200, "92.55");
    rnaAliquot.put(Columns.PURPOSE, "Validation");

    rnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaAliquot.put(Columns.EXTERNAL_NAME, "ext13"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, rnaAliquot.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    rnaAliquot.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    rnaAliquot.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleAliquot created = (SampleAliquot) getSession().get(SampleAliquotImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(rnaAliquot, created);
    assertDetailedSampleAttributes(rnaAliquot, created);
    assertSampleClass("whole RNA (aliquot)", created);
    assertAnalyteAttributes(rnaAliquot, created);
    assertAliquotAttributes(rnaAliquot, created);

    SampleStock stockParent = LimsUtils.getParent(SampleStock.class, created);
    assertStockAttributes(rnaAliquot, stockParent);
    assertRnaSampleAttributes(rnaAliquot, stockParent);

    SampleTissue tissueParent = LimsUtils.getParent(SampleTissue.class, created);
    assertTissueAttributes(rnaAliquot, tissueParent);
  }

  @Test
  public void testCreateOneRnaAliquotWithProject() throws Exception {
    // Goal: ensure one whole RNA (aliquot) associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, rAliquotClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> rnaAliquot = new HashMap<>();
    rnaAliquot.put(Columns.DESCRIPTION, "Description");
    rnaAliquot.put(Columns.RECEIVE_DATE, "2017-07-17");
    rnaAliquot.put(Columns.ID_BARCODE, "114"); // increment
    rnaAliquot.put(Columns.SAMPLE_TYPE, "GENOMIC");
    rnaAliquot.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    rnaAliquot.put(Columns.GROUP_ID, "1");
    rnaAliquot.put(Columns.GROUP_DESCRIPTION, "Test one");
    rnaAliquot.put(Columns.TISSUE_ORIGIN, "Bn (Brain)");
    rnaAliquot.put(Columns.TISSUE_TYPE, "P (Primary tumour)");
    rnaAliquot.put(Columns.TIMES_RECEIVED, "1");
    rnaAliquot.put(Columns.TUBE_NUMBER, "1");
    rnaAliquot.put(Columns.LAB, "BioBank (University Health Network)");
    rnaAliquot.put(Columns.EXT_INST_ID, "tube id 1");
    rnaAliquot.put(Columns.TISSUE_MATERIAL, "FFPE");
    rnaAliquot.put(Columns.REGION, "Medulla oblongata");
    rnaAliquot.put(Columns.STR_STATUS, "Submitted");
    rnaAliquot.put(Columns.DNASE_TREATED, "True");
    rnaAliquot.put(Columns.VOLUME, "10.0");
    rnaAliquot.put(Columns.CONCENTRATION, "3.75");
    rnaAliquot.put(Columns.QC_STATUS, "Ready");
    rnaAliquot.put(Columns.PURPOSE, "Library");

    rnaAliquot.forEach((k, v) -> table.enterText(k, 0, v));
    // need to enter this here, after project is entered otherwise identity lookup fails
    rnaAliquot.put(Columns.EXTERNAL_NAME, "ext14"); // increment
    table.enterText(Columns.EXTERNAL_NAME, 0, rnaAliquot.get(Columns.EXTERNAL_NAME));

    assertIdentityLookupWasSuccessful(page, table);

    page.clickSaveButton();

    assertSaveWasSuccessful(page, table);

    rnaAliquot.put(Columns.ALIAS, table.getText(Columns.ALIAS, 0));
    rnaAliquot.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

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

    List<String> sampleTypes = table.getDropdownOptions(Columns.SAMPLE_TYPE, 0);
    assertEquals(8, sampleTypes.size());
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(Columns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(Columns.SAMPLE_TYPE, 0));

    List<String> projects = table.getDropdownOptions(Columns.PROJECT, 0);
    assertTrue(projects.size() > 0);
    assertTrue(projects.contains("PRO1"));

    table.enterText(Columns.PROJECT, 0, "PRO1");
    assertEquals("PRO1", table.getText(Columns.PROJECT, 0));

    List<String> donorSexes = table.getDropdownOptions(Columns.DONOR_SEX, 0);
    assertEquals(5, donorSexes.size());
    assertTrue(donorSexes.contains("Female"));
    assertTrue(donorSexes.contains("Unspecified"));

    table.enterText(Columns.DONOR_SEX, 0, "Unspe");
    assertEquals("Unspecified", table.getText(Columns.DONOR_SEX, 0));

    List<String> qcStatuses = table.getDropdownOptions(Columns.QC_STATUS, 0);
    assertEquals(10, qcStatuses.size());
    assertTrue(qcStatuses.contains("Ready"));
    assertTrue(qcStatuses.contains("Refused Consent"));

    table.enterText(Columns.QC_STATUS, 0, "Rea");
    assertEquals("Ready", table.getText(Columns.QC_STATUS, 0));
  }

  @Test
  public void testCreateIdentityDependencyCells() throws Exception {
    // Goal: ensure that cells which depend on other columns are updated once the other columns are updated
    BulkSamplePage page = getCreatePage(1, null, identityClassId);
    HandsOnTable table = page.getTable();

    table.enterText(Columns.QC_NOTE, 0, "invisible");
    assertTrue("note is read-only", isStringEmptyOrNull(table.getText(Columns.QC_NOTE, 0)));

    table.enterText(Columns.QC_STATUS, 0, "Okd by Collaborator");
    table.enterText(Columns.QC_NOTE, 0, "writable note");
    assertEquals("note is writable", "writable note", table.getText(Columns.QC_NOTE, 0));
  }

  @Test
  /**
   * Need to run these tests after the other sample creates, because sample alias generation
   * (specifically, SampleNumberPerProject tracking) gets screwed up with manual identity creation.
   */
  public void testCreateOneIdentityNoProject() throws Exception {
    // Goal: ensure one identity can be saved
    BulkSamplePage page = getCreatePage(1, null, identityClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> identity = new HashMap<>();
    identity.put(Columns.ALIAS, "PRO1_1001");
    identity.put(Columns.SAMPLE_TYPE, "GENOMIC");
    identity.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    identity.put(Columns.PROJECT, "PRO1");
    identity.put(Columns.EXTERNAL_NAME, "ext1001"); // increment
    identity.put(Columns.DONOR_SEX, "Female");
    identity.put(Columns.QC_STATUS, "Ready");

    identity.forEach((k, v) -> table.enterText(k, 0, v));

    page.clickSaveButton();
    assertSaveWasSuccessful(page, table);

    identity.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes against what got saved to the database
    SampleIdentity created = (SampleIdentity) getSession().get(SampleIdentityImpl.class, Long.valueOf(newId));

    assertPlainSampleAttributes(identity, created);
    assertDetailedSampleAttributes(identity, created);
    assertSampleClass("Identity", created);
    assertIdentityAttributes(identity, created);
  }

  @Test
  /**
   * Need to run these tests after the other sample creates, because sample alias generation
   * (specifically, SampleNumberPerProject tracking) gets screwed up with manual identity creation.
   */
  public void testCreateOneIdentityWithProject() throws Exception {
    // Goal: ensure one identity associated with a predefined project can be saved
    BulkSamplePage page = getCreatePage(1, projectId, identityClassId);
    HandsOnTable table = page.getTable();

    Map<String, String> identity = new HashMap<>();
    identity.put(Columns.ALIAS, "PRO1_1002");
    identity.put(Columns.SAMPLE_TYPE, "GENOMIC");
    identity.put(Columns.SCIENTIFIC_NAME, "Homo sapiens");
    identity.put(Columns.EXTERNAL_NAME, "ext1002"); // increment
    identity.put(Columns.QC_STATUS, "Ready");

    identity.forEach((k, v) -> table.enterText(k, 0, v));

    page.clickSaveButton();
    assertSaveWasSuccessful(page, table);

    identity.forEach((k, v) -> assertEquals(v, table.getText(k, 0)));
    String newId = table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length());

    // verify attributes on the Edit single Sample page
    Project predefined = (Project) getSession().get(ProjectImpl.class, projectId);
    SampleIdentity created = (SampleIdentity) getSession().get(SampleIdentityImpl.class, Long.valueOf(newId));

    assertEquals("confirm project", predefined.getShortName(), created.getProject().getShortName());
    // everything else should be identical to testCreateOneIdentityNoProject
  }

  private void assertPlainSampleAttributes(Map<String, String> hotAttributes, Sample fromDb) {
    assertEquals("confirm project", hotAttributes.get(Columns.PROJECT), fromDb.getProject().getShortName());
    assertEquals("confirm alias", hotAttributes.get(Columns.ALIAS), fromDb.getAlias());
    assertEquals("confirm sample type", hotAttributes.get(Columns.SAMPLE_TYPE), fromDb.getSampleType());
    assertEquals("confirm scientific name", hotAttributes.get(Columns.SCIENTIFIC_NAME), fromDb.getScientificName());
    if (!LimsUtils.isIdentitySample(fromDb)) {
      assertEquals("confirm received date", hotAttributes.get(Columns.RECEIVE_DATE),
        LimsUtils.getDateAsString(fromDb.getReceivedDate()));
    }
  }

  private void assertDetailedSampleAttributes(Map<String, String> hotAttributes, DetailedSample fromDb) {
    assertEquals("confirm QC status", hotAttributes.get(Columns.QC_STATUS), fromDb.getDetailedQcStatus().getDescription());
    assertNotNull("parent is not null", fromDb.getAlias());
    assertEquals("confirm group ID", hotAttributes.get(Columns.GROUP_ID), fromDb.getGroupId());
    assertEquals("confirm group description", hotAttributes.get(Columns.GROUP_DESCRIPTION), fromDb.getGroupDescription());
  }

  private void assertSampleClass(String sampleClass, DetailedSample fromDb) {
    assertEquals("confirm sample class", sampleClass, fromDb.getSampleClass().getAlias());
  }

  private void assertIdentityAttributes(Map<String, String> hotAttributes, SampleIdentity fromDb) {
    assertEquals("confirm external name", hotAttributes.get(Columns.EXTERNAL_NAME), fromDb.getExternalName());
    assertEquals("confirm donor sex", hotAttributes.get(Columns.DONOR_SEX), fromDb.getDonorSex().getLabel());
  }

  private void assertTissueAttributes(Map<String, String> hotAttributes, SampleTissue fromDb) {
    assertEquals("confirm tissue material", hotAttributes.get(Columns.TISSUE_MATERIAL), fromDb.getTissueMaterial().getAlias());
    assertEquals("confirm region", hotAttributes.get(Columns.REGION), fromDb.getRegion());
    assertEquals("confirm ext inst id", hotAttributes.get(Columns.EXT_INST_ID), fromDb.getExternalInstituteIdentifier());
    assertEquals("confirm lab", hotAttributes.get(Columns.LAB), fromDb.getLab().getItemLabel());
    assertEquals("confirm tissue origin", hotAttributes.get(Columns.TISSUE_ORIGIN), fromDb.getTissueOrigin().getItemLabel());
    assertEquals("confirm tissue type", hotAttributes.get(Columns.TISSUE_TYPE), fromDb.getTissueType().getItemLabel());
    assertEquals("confirm passage number", hotAttributes.get(Columns.PASSAGE_NUMBER), fromDb.getPassageNumber());
    assertEquals("confirm times received", Integer.parseInt(hotAttributes.get(Columns.TIMES_RECEIVED)),
        fromDb.getTimesReceived().intValue());
    assertEquals("confirm tube number", Integer.parseInt(hotAttributes.get(Columns.TUBE_NUMBER)), fromDb.getTubeNumber().intValue());
  }

  private void assertSlideSampleAttributes(Map<String, String> hotAttributes, SampleSlide fromDb) {
    assertEquals("confirm slides", Integer.parseInt(hotAttributes.get(Columns.SLIDES)), fromDb.getSlides().intValue());
    assertEquals("confirm discards", Integer.parseInt(hotAttributes.get(Columns.DISCARDS)), fromDb.getDiscards().intValue());
    assertEquals("confirm thickness", Integer.parseInt(hotAttributes.get(Columns.THICKNESS)), fromDb.getThickness().intValue());
    assertEquals("confirm stain", hotAttributes.get(Columns.STAIN), fromDb.getStain().getName());
  }

  private void assertAnalyteAttributes(Map<String, String> hotAttributes, DetailedSample fromDb) {
    assertEquals("confirm volume", hotAttributes.get(Columns.VOLUME).toString(), fromDb.getVolume().toString());
    assertEquals("confirm concentration", hotAttributes.get(Columns.CONCENTRATION).toString(), fromDb.getConcentration().toString());
  }

  private void assertStockAttributes(Map<String, String> hotAttributes, SampleStock fromDb) {
    assertEquals("confirm STR Status", hotAttributes.get(Columns.STR_STATUS), fromDb.getStrStatus().getLabel());
  }

  private void assertRnaSampleAttributes(Map<String, String> hotAttributes, SampleStock fromDb) {
    assertEquals("confirm DNAse Treated", Boolean.valueOf(hotAttributes.get(Columns.DNASE_TREATED)),
        Boolean.valueOf(fromDb.getDNAseTreated()));
  }

  private void assertAliquotAttributes(Map<String, String> hotAttributes, SampleAliquot fromDb) {
    assertEquals("confirm purpose", hotAttributes.get(Columns.PURPOSE), fromDb.getSamplePurpose().getAlias());
  }

  private void assertIdentityLookupWasSuccessful(BulkSamplePage page, HandsOnTable table) {
    page.waitForIdentityLookup();
    assertEquals("identity lookup was successful", "First Receipt (PRO1)", table.getText(Columns.IDENTITY_ALIAS, 0));
  }

  private void assertSaveWasSuccessful(BulkSamplePage page, HandsOnTable table) {
    assertTrue("Sample was saved", page.getSuccessMessages().contains("Saved"));
    assertTrue("No errors are present", page.areErrorsHidden());

    assertTrue("Sample name has been generated", table.getText(Columns.NAME, 0).contains("SAM"));
    assertTrue("Sample alias has been generated", !isStringEmptyOrNull(table.getText(Columns.ALIAS, 0)));

  }

}
