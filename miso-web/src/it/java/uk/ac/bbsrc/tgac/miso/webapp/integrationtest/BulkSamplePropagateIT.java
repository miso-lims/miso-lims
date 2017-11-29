package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.SampleHandsOnTable;

public class BulkSamplePropagateIT extends AbstractBulkSampleIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  private BulkSamplePage getPropagatePage(Collection<Long> parentIds, Integer quantity, Long sampleClassId) {
    return BulkSamplePage.getForPropagate(getDriver(), getBaseUrl(), parentIds, quantity, sampleClassId);
  }

  @Test
  public void testPropagateTissueFromIdentity() {
    // goal: ensure one tissue can be propagated from one identity
    BulkSamplePage page = getPropagatePage(Arrays.asList(4441L), 1, tissueClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-10-27");
    attrs.put(SamColumns.ID_BARCODE, "4440-T");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    attrs.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    attrs.put(SamColumns.PASSAGE_NUMBER, "");
    attrs.put(SamColumns.TIMES_RECEIVED, "1");
    attrs.put(SamColumns.TUBE_NUMBER, "1");
    attrs.put(SamColumns.LAB, "BioBank (University Health Network)");
    attrs.put(SamColumns.SECONDARY_ID, "tube id 1");
    attrs.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    attrs.put(SamColumns.REGION, "Medulla oblongata");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Identity");
    attrs.put(SamColumns.SAMPLE_CLASS, "Tissue");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissue(attrs, getIdForRow(table, 0), true);
  }

  @Test
  public void testPropagateCurlsFromTissue() {
    // goal: ensure one curls can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, curlsClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "4440-Curls");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Tissue");
    attrs.put(SamColumns.SAMPLE_CLASS, "Curls");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(attrs, getIdForRow(table, 0), true);
  }

  @Test
  public void testPropagateSlideFromTissue() {
    // goal: ensure one slide can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, slideClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-Slide");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.SLIDES, "7");
    attrs.put(SamColumns.DISCARDS, "0");
    attrs.put(SamColumns.THICKNESS, "5");
    attrs.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Tissue");
    attrs.put(SamColumns.SAMPLE_CLASS, "Slide");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(attrs, getIdForRow(table, 0), true);
  }

  @Test
  public void testPropagateCdnaStockFromTissue() {
    // goal: ensure one cDNA stock can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, cStockClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-CDNA");
    attrs.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.STR_STATUS, "Submitted");
    attrs.put(SamColumns.VOLUME, "10.0");
    attrs.put(SamColumns.CONCENTRATION, "3.75");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Tissue");
    attrs.put(SamColumns.SAMPLE_CLASS, "cDNA (stock)");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForStock(attrs, getIdForRow(table, 0), true, false);
  }

  @Test
  public void testPropagateRnaStockFromTissue() {
    // goal: ensure one whole RNA stock can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, rStockClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-RNASTOCK");
    attrs.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.STR_STATUS, "Submitted");
    attrs.put(SamColumns.DNASE_TREATED, "True");
    attrs.put(SamColumns.VOLUME, "10.0");
    attrs.put(SamColumns.CONCENTRATION, "3.75");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Tissue");
    attrs.put(SamColumns.SAMPLE_CLASS, "whole RNA (stock)");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForStock(attrs, getIdForRow(table, 0), true, true);
  }

  @Test
  public void testPropagateLcmTubeFromSlide() {
    // goal: ensure one LCM Tube can be propagated from one slide
    BulkSamplePage page = getPropagatePage(Arrays.asList(4443L), 1, lcmTubeClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-LCM");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.SLIDES_CONSUMED, "1");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_SL01");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Slide");
    attrs.put(SamColumns.SAMPLE_CLASS, "LCM Tube");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(attrs, getIdForRow(table, 0), true);
  }

  @Test
  public void testPropagateGdnaStockFromLcmTube() {
    // goal: ensure one gDNA stock can be propagated from one LCM Tube
    BulkSamplePage page = getPropagatePage(Arrays.asList(4444L), 1, gStockClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "");
    attrs.put(SamColumns.ID_BARCODE, "PROP-GDNA");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.STR_STATUS, "Submitted");
    attrs.put(SamColumns.VOLUME, "10.0");
    attrs.put(SamColumns.CONCENTRATION, "3.75");
    attrs.put(SamColumns.QC_STATUS, "Ready");
    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_LCM01");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "LCM Tube");
    attrs.put(SamColumns.SAMPLE_CLASS, "gDNA (stock)");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForStock(attrs, getIdForRow(table, 0), true, false);
  }

  @Test
  public void testPropagateCdnaAliquotFromStock() {
    // goal: ensure one cDNA aliquot can be propagated from one cDNA stock
    BulkSamplePage page = getPropagatePage(Arrays.asList(4445L), 1, cAliquotClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-CDNA-ALIQUOT");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.PURPOSE, "Library");
    attrs.put(SamColumns.QC_STATUS, "Ready");
    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_D_S1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "cDNA (stock)");
    attrs.put(SamColumns.SAMPLE_CLASS, "cDNA (aliquot)");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForAliquot(attrs, getIdForRow(table, 0), true, false);
  }

  @Test
  public void testPropagateWholeRnaAliquotFromStock() {
    // goal: ensure one whole RNA aliquot can be propagated from one whole RNA stock
    BulkSamplePage page = getPropagatePage(Arrays.asList(4446L), 1, rAliquotClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-RNA-ALIQUOT");
    attrs.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.PURPOSE, "Extra");
    attrs.put(SamColumns.QC_STATUS, "Ready");
    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_R_S1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "whole RNA (stock)");
    attrs.put(SamColumns.SAMPLE_CLASS, "whole RNA (aliquot)");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForAliquot(attrs, getIdForRow(table, 0), true, true);
  }

  @Test
  public void testPropagateMrnaFromAliquot() {
    // goal: ensure one mRNA can be propagated from one whole RNA aliquot
    BulkSamplePage page = getPropagatePage(Arrays.asList(4447L), 1, mRnaClassId);
    SampleHandsOnTable table = page.getTable();

    Map<String, String> attrs = new HashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-MRNA");
    attrs.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.PURPOSE, "Research");
    attrs.put(SamColumns.QC_STATUS, "Ready");
    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_R_1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "whole RNA (aliquot)");
    attrs.put(SamColumns.SAMPLE_CLASS, "mRNA");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    saveSingleAndAssertSuccess(table);

    attrs.put(SamColumns.ALIAS, table.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, table.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, table.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForAliquot(attrs, getIdForRow(table, 0), true, true);
  }
}
