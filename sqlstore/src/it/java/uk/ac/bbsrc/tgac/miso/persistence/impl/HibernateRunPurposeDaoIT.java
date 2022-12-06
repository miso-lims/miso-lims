package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;

import java.util.Arrays;

public class HibernateRunPurposeDaoIT extends AbstractHibernateSaveDaoTest<RunPurpose, HibernateRunPurposeDao> {

  public HibernateRunPurposeDaoIT() {
    super(RunPurpose.class, 1L, 2);
  }

  @Override
  public HibernateRunPurposeDao constructTestSubject() {
    HibernateRunPurposeDao sut = new HibernateRunPurposeDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public RunPurpose getCreateItem() {
    RunPurpose purpose = new RunPurpose();
    purpose.setAlias("New Purpose");
    return purpose;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<RunPurpose, String> getUpdateParams() {
    return new UpdateParameters<>(1L, RunPurpose::getAlias, RunPurpose::setAlias, "Changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Research";
    RunPurpose purpose = getTestSubject().getByAlias(alias);
    assertNotNull(purpose);
    assertEquals(alias, purpose.getAlias());
  }

  @Test
  public void testGetUsageByPoolOrders() throws Exception {
    RunPurpose purpose1 = (RunPurpose) currentSession().get(RunPurpose.class, 1L);
    assertEquals(2, getTestSubject().getUsageByPoolOrders(purpose1));
    RunPurpose purpose2 = (RunPurpose) currentSession().get(RunPurpose.class, 2L);
    assertEquals(0, getTestSubject().getUsageByPoolOrders(purpose2));
  }

  @Test
  public void testGetUsageBySequencingOrders() throws Exception {
    RunPurpose purpose1 = (RunPurpose) currentSession().get(RunPurpose.class, 1L);
    assertEquals(2, getTestSubject().getUsageBySequencingOrders(purpose1));
    RunPurpose purpose2 = (RunPurpose) currentSession().get(RunPurpose.class, 2L);
    assertEquals(0, getTestSubject().getUsageBySequencingOrders(purpose2));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateRunPurposeDao::listByIdList, Arrays.asList(1L, 2L));
  }

}
