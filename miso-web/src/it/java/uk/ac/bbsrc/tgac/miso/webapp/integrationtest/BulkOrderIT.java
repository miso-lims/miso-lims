package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrderImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkOrderPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkOrderPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkOrderIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testCreateSetup() {
    BulkOrderPage page = BulkOrderPage.getForCreate(getDriver(), getBaseUrl(), Sets.newHashSet(120001L, 120002L, 120003L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    List<String> expectedColumns = BulkOrderPage.Columns.all();
    assertEquals(expectedColumns.size(), headings.size());
    for (String col : expectedColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(3, table.getRowCount());
  }

  @Test
  public void testCreate() {
    BulkOrderPage page = BulkOrderPage.getForCreate(getDriver(), getBaseUrl(), Sets.newHashSet(120001L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "IPO120001");
    attrs.put(Columns.ALIAS, "1IPO_POOL_1");
    assertColumnValues(table, 0, attrs, "initial");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.PLATFORM, "Illumina HiSeq 2500");
    changes.put(Columns.PARAMETERS, "1x151");
    changes.put(Columns.PARTITIONS, "3");
    fillRow(table, 0, changes);

    changes.putAll(attrs);
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");

    Pool pool = (Pool) getSession().get(PoolImpl.class, 120001L);
    @SuppressWarnings("unchecked")
    List<PoolOrder> orders = getSession().createCriteria(PoolOrderImpl.class)
        .add(Restrictions.eq("pool", pool))
        .list();
    assertEquals(1, orders.size());
    assertEquals(changes.get(Columns.PARAMETERS), orders.get(0).getSequencingParameter().getName());
    assertEquals(Integer.valueOf(3), orders.get(0).getPartitions());
  }

}
