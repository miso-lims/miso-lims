package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ListTarget;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.RunPage.PoolSearch;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class OrdersIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }
  
  @Test
  public void testListOrdersDisplay() {
    // confirm that correct number of lanes for each status are shown
    ListTabbedPage page = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), ListTarget.ORDERS);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertTrue("Check for column 'Requested': ", headings.contains(Columns.REQUESTED));
    assertTrue("Check for column 'Remaining': ", headings.contains(Columns.REMAINING));
    assertEquals("check for number requested", "2", table.getTextByOtherLocator(Columns.REQUESTED, Columns.ALIAS, "Orders_Test_Pool_1"));
    assertEquals("check for number remaining", "2", table.getTextByOtherLocator(Columns.REMAINING, Columns.ALIAS, "Orders_Test_Pool_1"));
    assertEquals("check for chemistry", "v3 1x101",
        table.getTextByOtherLocator(Columns.SEQUENCING_PARAMETERS, Columns.ALIAS, "Orders_Test_Pool_1"));
  }

  @Test
  public void testAddPoolToRunChangesOrders() {
    // goal: confirm that adding a pool to a run changes its order completion status
    ListTabbedPage orderPage1 = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), ListTarget.ORDERS);
    DataTable orderTable1 = orderPage1.getTable();
    assertEquals("check for number requested", "2", orderTable1.getTextByOtherLocator(Columns.REQUESTED, Columns.NAME, "IPO602"));
    assertEquals("check for number remaining", "2", orderTable1.getTextByOtherLocator(Columns.REMAINING, Columns.NAME, "IPO602"));

    RunPage run = RunPage.getForEdit(getDriver(), getBaseUrl(), 602L);
    run.assignPools(Arrays.asList(0), PoolSearch.SEARCH, "IPO602");

    ListTabbedPage orderPage2 = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), ListTarget.ORDERS);
    DataTable orderTable2 = orderPage2.getTable();
    assertEquals("check for number requested", "2", orderTable2.getTextByOtherLocator(Columns.REQUESTED, Columns.NAME, "IPO602"));
    assertEquals("check for number remaining", "1", orderTable2.getTextByOtherLocator(Columns.REMAINING, Columns.NAME, "IPO602"));
    assertEquals("check for number running", "1", orderTable2.getTextByOtherLocator(Columns.RUNNING, Columns.NAME, "IPO602"));

    // confirm that changing the run's status changes the order's completion states
    RunPage run2 = RunPage.getForEdit(getDriver(), getBaseUrl(), 602L);
    run2.setField(Field.STATUS, "Completed");
    run2.setField(Field.COMPLETION_DATE, "2017-10-20");
    run2.save();

    ListTabbedPage orderPage3 = ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), ListTarget.ORDERS);
    DataTable orderTable3 = orderPage3.getTable();
    List<String> headings = orderTable3.getColumnHeadings();
    assertFalse(headings.contains(Columns.RUNNING));
    assertEquals("check for number requested", "2", orderTable3.getTextByOtherLocator(Columns.REQUESTED, Columns.NAME, "IPO602"));
    assertEquals("check for number remaining", "1", orderTable3.getTextByOtherLocator(Columns.REMAINING, Columns.NAME, "IPO602"));
    assertEquals("check for number completed", "1", orderTable3.getTextByOtherLocator(Columns.COMPLETED, Columns.NAME, "IPO602"));
  }
}
