package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;


public class HibernateDeliverableDaoIT extends AbstractHibernateSaveDaoTest<Deliverable, HibernateDeliverableDao> {

  public HibernateDeliverableDaoIT() {
    super(Deliverable.class, 1L, 3);
  }

  @Override
  public HibernateDeliverableDao constructTestSubject() {
    HibernateDeliverableDao sut = new HibernateDeliverableDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public Deliverable getCreateItem() {
    Deliverable deliverable = new Deliverable();
    deliverable.setName("New Deliverable");
    return deliverable;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Deliverable, String> getUpdateParams() {
    return new UpdateParameters<>(1L, Deliverable::getName, Deliverable::setName, "New Name");
  }

  @Test
  public void testGetByName() throws Exception {
    String name = "deliverable1";
    Deliverable deliverable = getTestSubject().getByName(name);
    assertNotNull(deliverable);
    assertEquals(name, deliverable.getName());
  }

  @Test
  public void testGetUsage() throws Exception {
    Deliverable deliverable = (Deliverable) currentSession().get(Deliverable.class, 1L);
    assertEquals(1L, getTestSubject().getUsage(deliverable));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateDeliverableDao::listByIdList, Arrays.asList(1L, 2L));
  }
}
