package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateLabDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateLabDao dao;

  @Before
  public void setup() {
    dao = new HibernateLabDao();
    dao.setEntityManager(entityManager);
  }

  @Test
  public void testGetLabList() throws IOException {
    List<Lab> list = dao.list();
    assertNotNull(list);
    assertEquals(4, list.size());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(2L, 3L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

  @Test
  public void testGetSingleLab() throws Exception {
    Lab l = dao.get(1L);
    assertNotNull(l);
    assertEquals(1L, l.getId());
    assertEquals("Institute A - Lab A1", l.getAlias());
  }

  @Test
  public void testGetSingleLabNull() throws Exception {
    Lab l = dao.get(100L);
    assertNull(l);
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Institute B - Lab B1";
    Lab lab = dao.getByAlias(alias);
    assertNotNull(lab);
    assertEquals(alias, lab.getAlias());
  }

  @Test
  public void testAddLab() throws Exception {
    Lab l = new LabImpl();
    l.setAlias("NewLab");
    User user = new UserImpl();
    user.setId(1L);
    l.setCreator(user);
    l.setLastModifier(user);
    Date now = new Date();
    l.setCreationTime(now);
    l.setLastModified(now);

    final long newId = dao.create(l);
    Lab saved = dao.get(newId);
    assertNotNull(saved);
    assertEquals(l.getAlias(), saved.getAlias());
  }

  @Test
  public void testUpdateLab() throws Exception {
    Lab l = dao.get(1L);
    final String newAlias = "Changed Alias";
    l.setAlias(newAlias);

    dao.update(l);
    Lab updated = dao.get(1L);
    assertEquals(newAlias, updated.getAlias());
  }

  @Test
  public void testGetUsageByTissues() throws Exception {
    Lab lab = dao.get(2L);
    assertEquals(1L, dao.getUsageByTissues(lab));
  }

  @Test
  public void testGetUsageByTransfers() throws Exception {
    Lab lab = dao.get(1L);
    assertEquals(2L, dao.getUsageByTransfers(lab));
  }

}
