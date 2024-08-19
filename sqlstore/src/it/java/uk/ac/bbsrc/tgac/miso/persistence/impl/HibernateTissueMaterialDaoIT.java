package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

import java.util.Arrays;

public class HibernateTissueMaterialDaoIT
    extends AbstractHibernateSaveDaoTest<TissueMaterial, HibernateTissueMaterialDao> {

  public HibernateTissueMaterialDaoIT() {
    super(TissueMaterialImpl.class, 3L, 3);
  }

  @Override
  public HibernateTissueMaterialDao constructTestSubject() {
    HibernateTissueMaterialDao sut = new HibernateTissueMaterialDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public TissueMaterial getCreateItem() {
    TissueMaterial material = new TissueMaterialImpl();
    material.setAlias("New Mat");
    User user = (User) currentSession().get(UserImpl.class, 1L);
    material.setChangeDetails(user);
    return material;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<TissueMaterial, String> getUpdateParams() {
    return new UpdateParameters<>(3L, TissueMaterial::getAlias, TissueMaterial::setAlias, "Changed");
  }

  @Test
  public void testGetUsage() throws Exception {
    TissueMaterial material1 = (TissueMaterial) currentSession().get(TissueMaterialImpl.class, 1L);
    assertEquals(0, getTestSubject().getUsage(material1));
    TissueMaterial material2 = (TissueMaterial) currentSession().get(TissueMaterialImpl.class, 2L);
    assertEquals(2, getTestSubject().getUsage(material2));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateTissueMaterialDao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(getTestSubject()::listByIdList);
  }

}
