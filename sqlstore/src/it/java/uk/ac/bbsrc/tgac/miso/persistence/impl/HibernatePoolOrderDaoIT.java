package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernatePoolOrderDaoIT extends AbstractDAOTest {

  private HibernatePoolOrderDao sut;

  @Before
  public void setup() {
    sut = new HibernatePoolOrderDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    PoolOrder order = sut.get(id);
    assertNotNull(order);
    assertEquals(id, order.getId());
  }

  @Test
  public void testList() throws IOException {
    List<PoolOrder> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New Category";
    PoolOrder order = new PoolOrder();
    order.setAlias(alias);
    Date now = new Date();
    order.setCreationTime(now);
    order.setLastModified(now);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    order.setCreator(user);
    order.setLastModifier(user);
    RunPurpose purpose = (RunPurpose) currentSession().get(RunPurpose.class, 1L);
    order.setPurpose(purpose);
    long savedId = sut.create(order);

    clearSession();

    PoolOrder saved = (PoolOrder) currentSession().get(PoolOrder.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Alias";
    PoolOrder order = (PoolOrder) currentSession().get(PoolOrder.class, id);
    assertNotEquals(alias, order.getAlias());
    order.setAlias(alias);
    sut.update(order);

    clearSession();

    PoolOrder saved = (PoolOrder) currentSession().get(PoolOrder.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testListByPoolId() throws Exception {
    List<PoolOrder> orders = sut.getAllByPoolId(2L);
    assertEquals(1, orders.size());
  }

}
