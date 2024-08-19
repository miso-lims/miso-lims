package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateTissueTypeDaoIT extends AbstractDAOTest {

  private HibernateTissueTypeDao sut;

  @Before
  public void setup() {
    sut = new HibernateTissueTypeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    TissueType tt = sut.get(id);
    assertNotNull(tt);
    assertEquals(id, tt.getId());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "Test Type";
    TissueType tt = sut.getByAlias(alias);
    assertNotNull(tt);
    assertEquals(alias, tt.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<TissueType> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New Type";
    TissueType tt = new TissueTypeImpl();
    tt.setAlias(alias);
    tt.setDescription("descriptive stuff");
    User user = (User) currentSession().get(UserImpl.class, 1L);
    tt.setChangeDetails(user);
    long savedId = sut.create(tt);

    clearSession();

    TissueType saved = (TissueType) currentSession().get(TissueTypeImpl.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Name";
    TissueType tt = (TissueType) currentSession().get(TissueTypeImpl.class, id);
    assertNotEquals(alias, tt.getAlias());
    tt.setAlias(alias);
    sut.update(tt);

    clearSession();

    TissueType saved = (TissueType) currentSession().get(TissueTypeImpl.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    TissueType tt = (TissueType) currentSession().get(TissueTypeImpl.class, 1L);
    assertEquals("Test Type", tt.getAlias());
    assertEquals(5L, sut.getUsage(tt));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(1L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
