package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkSamplePropagateIT extends AbstractBulkSampleIT {

  private BulkSamplePage getPropagatePage(List<Long> parentIds, Integer quantity, String sampleCategory) {
    return BulkSamplePage.getForPropagate(getDriver(), getBaseUrl(), parentIds, Arrays.asList(quantity),
        sampleCategory);
  }

  @Test
  public void testPropagateTissueFromIdentity() {
    // goal: ensure one tissue can be propagated from one identity
    BulkSamplePage page = getPropagatePage(Arrays.asList(4441L), 1, SampleTissue.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
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
    attrs.put(SamColumns.SECONDARY_ID, "tube id 1");
    attrs.put(SamColumns.TISSUE_MATERIAL, "FFPE");
    attrs.put(SamColumns.REGION, "Medulla oblongata");
    attrs.put(SamColumns.QC_STATUS, "Ready");
    attrs.put(SamColumns.CREATION_DATE, "2018-07-17");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Identity");
    attrs.put(SamColumns.SAMPLE_CLASS, "Tissue");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissue(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateTissuePieceFromSlide() {
    // goal: ensure one curls can be propagated from one slide
    BulkSamplePage page = getPropagatePage(Arrays.asList(4443L), 1, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "4440-Curls");
    attrs.put(SamColumns.SAMPLE_CLASS, "Tissue Piece");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.QC_STATUS, "Ready");
    attrs.put(SamColumns.PIECE_TYPE, "Curls");
    attrs.put(SamColumns.SLIDES_CONSUMED, "2");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_SL01");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Slide");
    attrs.put(SamColumns.SAMPLE_CLASS, "Tissue Piece");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateSlideFromTissue() {
    // goal: ensure one slide can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-Slide");
    attrs.put(SamColumns.SAMPLE_CLASS, "Slide");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.SLIDES, "7");
    attrs.put(SamColumns.THICKNESS, "5");
    attrs.put(SamColumns.STAIN, "Hematoxylin+Eosin");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Tissue");
    attrs.put(SamColumns.SAMPLE_CLASS, "Slide");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateCdnaStockFromTissue() {
    // goal: ensure one cDNA stock can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-CDNA");
    attrs.put(SamColumns.SAMPLE_CLASS, "cDNA (stock)");
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
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForStock(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateRnaStockFromTissue() {
    // goal: ensure one whole RNA stock can be propagated from one tissue
    BulkSamplePage page = getPropagatePage(Arrays.asList(4442L), 1, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-RNASTOCK");
    attrs.put(SamColumns.SAMPLE_CLASS, "whole RNA (stock)");
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
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForRnaStock(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateLcmTubeFromSlide() {
    // goal: ensure one LCM Tube can be propagated from one slide
    BulkSamplePage page = getPropagatePage(Arrays.asList(4443L), 1, SampleTissueProcessing.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-LCM");
    attrs.put(SamColumns.SAMPLE_CLASS, "Tissue Piece");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.GROUP_ID, "1");
    attrs.put(SamColumns.GROUP_DESCRIPTION, "Test one");
    attrs.put(SamColumns.SLIDES_CONSUMED, "1");
    attrs.put(SamColumns.PIECE_TYPE, "LCM Tube");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));

    // add in fields that are read-only
    attrs.put(SamColumns.PARENT_ALIAS, "PROP_0001_nn_n_1-1_SL01");
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Slide");
    attrs.put(SamColumns.SAMPLE_CLASS, "Tissue Piece");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForTissueProcessing(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateGdnaStockFromLcmTube() {
    // goal: ensure one gDNA stock can be propagated from one LCM Tube
    BulkSamplePage page = getPropagatePage(Arrays.asList(4444L), 1, SampleStock.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "");
    attrs.put(SamColumns.ID_BARCODE, "PROP-GDNA");
    attrs.put(SamColumns.SAMPLE_CLASS, "gDNA (stock)");
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
    attrs.put(SamColumns.PARENT_SAMPLE_CLASS, "Tissue Piece");
    attrs.put(SamColumns.SAMPLE_CLASS, "gDNA (stock)");

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    // verify attributes against what got saved to the database
    assertAllForStock(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateCdnaAliquotFromStock() {
    // goal: ensure one cDNA aliquot can be propagated from one cDNA stock
    BulkSamplePage page = getPropagatePage(Arrays.asList(4445L), 1, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-CDNA-ALIQUOT");
    attrs.put(SamColumns.SAMPLE_CLASS, "cDNA (aliquot)");
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

    attrs.forEach((k, v) -> assertEquals("pre-save", v, table.getText(k, 0)));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForAliquot(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateWholeRnaAliquotFromStock() {
    // goal: ensure one whole RNA aliquot can be propagated from one whole RNA stock
    BulkSamplePage page = getPropagatePage(Arrays.asList(4446L), 1, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-RNA-ALIQUOT");
    attrs.put(SamColumns.SAMPLE_CLASS, "whole RNA (aliquot)");
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
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForRnaAliquot(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateMrnaFromAliquot() {
    // goal: ensure one mRNA can be propagated from one whole RNA aliquot
    BulkSamplePage page = getPropagatePage(Arrays.asList(4447L), 1, SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.DESCRIPTION, "Description");
    attrs.put(SamColumns.CREATION_DATE, "2017-07-17");
    attrs.put(SamColumns.ID_BARCODE, "PROP-MRNA");
    attrs.put(SamColumns.SAMPLE_CLASS, "mRNA");
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
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    attrs.put(SamColumns.ALIAS, savedTable.getText(SamColumns.ALIAS, 0));
    attrs.put(SamColumns.NAME, savedTable.getText(SamColumns.NAME, 0));
    attrs.forEach((k, v) -> assertEquals("Checking value of column '" + k + "'", v, savedTable.getText(k, 0)));
    // verify attributes against what got saved to the database
    assertAllForRnaAliquot(attrs, getIdForRow(savedTable, 0), true);
  }

  @Test
  public void testPropagateSpecifiedReplicates() {
    BulkSamplePage page =
        BulkSamplePage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100003L, 110003L, 120003L),
            Arrays.asList(1, 2, 3), SampleAliquot.CATEGORY_NAME);
    HandsOnTable table = page.getTable();
    assertEquals(6, table.getRowCount());
    assertEquals("LIBT_0001_Ly_P_1-1_D_S1", table.getText(SamColumns.PARENT_ALIAS, 0));
    assertEquals("1LIB_0001_Ly_P_1-1_D_S1", table.getText(SamColumns.PARENT_ALIAS, 1));
    assertEquals("1LIB_0001_Ly_P_1-1_D_S1", table.getText(SamColumns.PARENT_ALIAS, 2));
    assertEquals("1IPO_0001_Ly_P_1-1_D_S1", table.getText(SamColumns.PARENT_ALIAS, 3));
    assertEquals("1IPO_0001_Ly_P_1-1_D_S1", table.getText(SamColumns.PARENT_ALIAS, 4));
    assertEquals("1IPO_0001_Ly_P_1-1_D_S1", table.getText(SamColumns.PARENT_ALIAS, 5));
  }
}
