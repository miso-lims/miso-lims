package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;

import java.util.Arrays;

public class HibernateTissueOriginDaoIT extends AbstractHibernateSaveDaoTest<TissueOrigin, HibernateTissueOriginDao> {

  public HibernateTissueOriginDaoIT() {
    super(TissueOriginImpl.class, 1L, 2);
  }

  @Override
  public HibernateTissueOriginDao constructTestSubject() {
    HibernateTissueOriginDao sut = new HibernateTissueOriginDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public TissueOrigin getCreateItem() {
    TissueOrigin origin = new TissueOriginImpl();
    origin.setAlias("TO");
    origin.setDescription("Test Origin");
    User user = (User) currentSession().get(UserImpl.class, 1L);
    origin.setChangeDetails(user);
    return origin;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<TissueOrigin, String> getUpdateParams() {
    return new UpdateParameters<>(1L, TissueOrigin::getDescription, TissueOrigin::setDescription, "Changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Test Origin";
    TissueOrigin origin = getTestSubject().getByAlias(alias);
    assertNotNull(origin);
    assertEquals(alias, origin.getAlias());
  }

  @Test
  public void testGetUsage() throws Exception {
    TissueOrigin origin1 = (TissueOrigin) currentSession().get(TissueOriginImpl.class, 1L);
    assertEquals(5, getTestSubject().getUsage(origin1));
    TissueOrigin origin2 = (TissueOrigin) currentSession().get(TissueOriginImpl.class, 2L);
    assertEquals(0, getTestSubject().getUsage(origin2));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(TissueOriginDao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(getTestSubject()::listByIdList);
  }

}
