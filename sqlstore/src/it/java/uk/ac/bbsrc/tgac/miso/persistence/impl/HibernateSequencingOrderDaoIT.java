package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingOrderImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSequencingOrderDaoIT
    extends AbstractHibernateSaveDaoTest<SequencingOrder, HibernateSequencingOrderDao> {

  public HibernateSequencingOrderDaoIT() {
    super(SequencingOrderImpl.class, 2L, 2);
  }

  @Override
  public HibernateSequencingOrderDao constructTestSubject() {
    HibernateSequencingOrderDao sut = new HibernateSequencingOrderDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public SequencingOrder getCreateItem() {
    SequencingOrder order = new SequencingOrderImpl();
    order.setPool((Pool) currentSession().get(PoolImpl.class, 1L));
    order.setSequencingParameters((SequencingParameters) currentSession().get(SequencingParameters.class, 1L));
    order.setPartitions(2);
    order.setPurpose((RunPurpose) currentSession().get(RunPurpose.class, 1L));
    order.setChangeDetails((User) currentSession().get(UserImpl.class, 1L));
    return order;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<SequencingOrder, String> getUpdateParams() {
    return new UpdateParameters<>(1L, SequencingOrder::getDescription, SequencingOrder::setDescription, "Changed");
  }

  @Test
  public void testListByPool() throws Exception {
    Pool pool1 = (Pool) currentSession().get(PoolImpl.class, 1L);
    assertEquals(2, getTestSubject().listByPool(pool1).size());
    Pool pool2 = (Pool) currentSession().get(PoolImpl.class, 2L);
    assertEquals(0, getTestSubject().listByPool(pool2).size());
  }

  @Test
  public void testListByAttributes() throws Exception {
    Pool pool = (Pool) currentSession().get(PoolImpl.class, 1L);
    RunPurpose purpose = (RunPurpose) currentSession().get(RunPurpose.class, 1L);
    SequencingContainerModel containerModel =
        (SequencingContainerModel) currentSession().get(SequencingContainerModel.class, 1L);
    SequencingParameters parameters = (SequencingParameters) currentSession().get(SequencingParameters.class, 1L);
    assertEquals(1, getTestSubject().listByAttributes(pool, purpose, null, parameters, 1).size());
    assertEquals(1, getTestSubject().listByAttributes(pool, purpose, containerModel, parameters, 2).size());
    assertEquals(0, getTestSubject().listByAttributes(pool, purpose, null, parameters, 10).size());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateSequencingOrderDao::listByIdList, Arrays.asList(1L, 2L));
  }

}
