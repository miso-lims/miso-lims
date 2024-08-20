package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.impl.QueryBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSequencingOrderPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSequencingOrderPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkSequencingOrderIT extends AbstractIT {

  @Before
  public void setup() {
    login();
  }

  @Test
  public void testCreateSetup() {
    BulkSequencingOrderPage page =
        BulkSequencingOrderPage.getForCreate(getDriver(), getBaseUrl(), Sets.newHashSet(120001L, 120002L, 120003L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    List<String> expectedColumns = BulkSequencingOrderPage.Columns.all();
    assertEquals(expectedColumns.size(), headings.size());
    for (String col : expectedColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(3, table.getRowCount());
  }

  @Test
  public void testCreate() {
    BulkSequencingOrderPage page =
        BulkSequencingOrderPage.getForCreate(getDriver(), getBaseUrl(), Sets.newHashSet(120001L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "IPO120001");
    attrs.put(Columns.ALIAS, "1IPO_POOL_1");
    assertColumnValues(table, 0, attrs, "initial");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.PURPOSE, "Production");
    changes.put(Columns.INSTRUMENT_MODEL, "Illumina HiSeq 2500");
    changes.put(Columns.CONTAINER_MODEL, "Generic 8-Lane Illumina Flow Cell");
    changes.put(Columns.PARAMETERS, "1x151");
    changes.put(Columns.PARTITIONS, "3");
    changes.put(Columns.DESCRIPTION, "test");
    fillRow(table, 0, changes);

    changes.putAll(attrs);
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    changes.remove(Columns.INSTRUMENT_MODEL); // not shown after save as it's not actually saved
    assertColumnValues(savedTable, 0, changes, "post-save");

    Pool pool = (Pool) getSession().get(PoolImpl.class, 120001L);

    QueryBuilder<SequencingOrder, SequencingOrderImpl> builder =
        new QueryBuilder<>(getSession(), SequencingOrderImpl.class, SequencingOrder.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SequencingOrderImpl_.pool), pool));
    List<SequencingOrder> orders = builder.getResultList();

    assertEquals(1, orders.size());
    assertEquals(changes.get(Columns.PARAMETERS), orders.get(0).getSequencingParameter().getName());
    assertEquals(Integer.valueOf(3), orders.get(0).getPartitions());
    assertEquals("test", orders.get(0).getDescription());
  }

}
