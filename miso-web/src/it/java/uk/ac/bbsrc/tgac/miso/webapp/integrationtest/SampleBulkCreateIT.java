package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class SampleBulkCreateIT extends AbstractIT {

  private static final Set<String> identityColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.RECEIVE_DATE,
      Columns.ID_BARCODE, Columns.SAMPLE_TYPE, Columns.SCIENTIFIC_NAME, Columns.PROJECT, Columns.EXTERNAL_NAME, Columns.DONOR_SEX,
      Columns.SAMPLE_CLASS, Columns.GROUP_ID, Columns.GROUP_DESCRIPTION, Columns.QC_STATUS, Columns.QC_NOTE);

  @Before
  public void setup() {
    loginAdmin();
  }

  private BulkSamplePage getCreatePage(Integer quantity, Long projectId, Long sampleClassId) {
    return BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), quantity, projectId, sampleClassId);
  }

  @Test
  public void testCreateIdentitySetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkSamplePage page = getCreatePage(1, null, 1L);
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
    BulkSamplePage page = getCreatePage(1, null, 1L);
    HandsOnTable table = page.getTable();

    List<String> sampleTypes = table.getDropdownOptions(Columns.SAMPLE_TYPE, 0);
    assertEquals(8, sampleTypes.size());
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("TRANSCRIPTOMIC"));

    table.enterText(Columns.SAMPLE_TYPE, 0, "GENOM");
    assertEquals("GENOMIC", table.getText(Columns.SAMPLE_TYPE, 0));

    // TODO: check other columns
  }

}
