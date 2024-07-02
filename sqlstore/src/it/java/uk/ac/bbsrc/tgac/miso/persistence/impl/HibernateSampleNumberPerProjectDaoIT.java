package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSampleNumberPerProjectDaoIT extends AbstractDAOTest {

  private static final String PRO1_PARTIAL_ALIAS = "PRO1_";
  private static final String PRO2_PARTIAL_ALIAS = "PRO2_";
  private static final String PRO3_PARTIAL_ALIAS = "PRO3_";

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateSampleNumberPerProjectDao sampleNumberPerProjectDao;

  @Before
  public void setup() {
    sampleNumberPerProjectDao = new HibernateSampleNumberPerProjectDao();
    sampleNumberPerProjectDao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testNextNumber() throws Exception {
    Project project = new ProjectImpl();
    project.setId(1L);
    User user = new UserImpl();
    user.setId(1L);
    String s = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0002", s);
  }

  @Test
  public void testNextNumberMultipleIncrements() throws Exception {
    Project project = new ProjectImpl();
    project.setId(1L);
    User user = new UserImpl();
    user.setId(1L);
    String s1 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0002", s1);

    String s2 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0003", s2);

    String s3 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0004", s3);
  }

  @Test
  public void testNextNumberPaddingDecreasesTest() throws Exception {
    Project project = new ProjectImpl();
    project.setId(1L);
    User user = new UserImpl();
    user.setId(1L);
    String s1 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0002", s1);

    String s2 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0003", s2);

    String s3 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0004", s3);

    String s4 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0005", s4);

    String s5 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0006", s5);

    String s6 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0007", s6);

    String s7 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0008", s7);

    String s8 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0009", s8);

    String s9 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0010", s9);

    String s10 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0011", s10);

    String s11 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0012", s11);
  }

  @Test
  public void testNextNumberNoExistingNumber() throws Exception {
    Project project = new ProjectImpl();
    project.setId(2L);
    User user = new UserImpl();
    user.setId(1L);
    String s = sampleNumberPerProjectDao.nextNumber(project, user, PRO2_PARTIAL_ALIAS);
    assertEquals("0001", s);
  }

  @Test
  public void testNextNumberNoExistingNumber2() throws Exception {
    Project project = new ProjectImpl();
    project.setId(2L);
    User user = new UserImpl();
    user.setId(1L);
    String s = sampleNumberPerProjectDao.nextNumber(project, user, PRO2_PARTIAL_ALIAS);
    assertEquals("0001", s);

    String s2 = sampleNumberPerProjectDao.nextNumber(project, user, PRO2_PARTIAL_ALIAS);
    assertEquals("0002", s2);
  }

  @Test
  public void testNextNumberTwoProjects() throws Exception {
    Project project1 = new ProjectImpl();
    project1.setId(1L);
    User user = new UserImpl();
    user.setId(1L);

    Project project2 = new ProjectImpl();
    project2.setId(2L);

    String s1 = sampleNumberPerProjectDao.nextNumber(project1, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0002", s1);

    String s2 = sampleNumberPerProjectDao.nextNumber(project1, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0003", s2);

    String s3 = sampleNumberPerProjectDao.nextNumber(project2, user, PRO2_PARTIAL_ALIAS);
    assertEquals("0001", s3);

    String s4 = sampleNumberPerProjectDao.nextNumber(project1, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0004", s4);

    String s5 = sampleNumberPerProjectDao.nextNumber(project2, user, PRO2_PARTIAL_ALIAS);
    assertEquals("0002", s5);

    assertTrue(sampleNumberPerProjectDao.list().size() == 3);

  }

  @Test
  public void testExceedPadding() throws Exception {
    Project project = new ProjectImpl();
    project.setId(3L);
    User user = new UserImpl();
    user.setId(1L);
    String s = sampleNumberPerProjectDao.nextNumber(project, user, PRO3_PARTIAL_ALIAS);
    assertEquals("10000", s);

    String s2 = sampleNumberPerProjectDao.nextNumber(project, user, PRO3_PARTIAL_ALIAS);
    assertEquals("10001", s2);
  }

  @Test
  public void testAvoidManualInsert() throws Exception {
    Project project = new ProjectImpl();
    project.setId(1L);
    User user = new UserImpl();
    user.setId(1L);
    String s = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0002", s);

    Sample sam = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 1L);
    sam.setAlias(PRO1_PARTIAL_ALIAS + "0003");
    sessionFactory.getCurrentSession().save(sam);

    String s2 = sampleNumberPerProjectDao.nextNumber(project, user, PRO1_PARTIAL_ALIAS);
    assertEquals("0004", s2);
  }

}
