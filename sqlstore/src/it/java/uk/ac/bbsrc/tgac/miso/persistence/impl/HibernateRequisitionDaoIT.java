package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateRequisitionDaoIT extends AbstractHibernateSaveDaoTest<Requisition, HibernateRequisitionDao> {

  public HibernateRequisitionDaoIT() {
    super(Requisition.class, 1L, 2);
  }

  @Override
  public HibernateRequisitionDao constructTestSubject() {
    HibernateRequisitionDao sut = new HibernateRequisitionDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public Requisition getCreateItem() {
    Requisition req = new Requisition();
    req.setAlias("New Req");
    Assay assay = (Assay) currentSession().get(Assay.class, 1L);
    req.setAssay(assay);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    Date date = new Date();
    req.setCreator(user);
    req.setCreationTime(date);
    req.setLastModifier(user);
    req.setLastModified(date);
    return req;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Requisition, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Requisition::getAlias, Requisition::setAlias, "Changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Detailed Req";
    Requisition req = getTestSubject().getByAlias(alias);
    assertNotNull(req);
    assertEquals(alias, req.getAlias());
  }

}
