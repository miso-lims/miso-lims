package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSampleClassDaoIT extends AbstractDAOTest {

  private HibernateSampleClassDao sut;

  @Before
  public void setup() {
    sut = new HibernateSampleClassDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    SampleClass sampleClass = sut.get(id);
    assertNotNull(sampleClass);
    assertEquals(id, sampleClass.getId());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "Primary Tumor Tissue";
    SampleClass sampleClass = sut.getByAlias(alias);
    assertNotNull(sampleClass);
    assertEquals(alias, sampleClass.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<SampleClass> list = sut.list();
    assertNotNull(list);
    assertEquals(5, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New SampleClass";
    SampleClass sampleClass = new SampleClassImpl();
    sampleClass.setAlias(alias);
    sampleClass.setSampleCategory(SampleTissue.CATEGORY_NAME);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    Date now = new Date();
    sampleClass.setCreator(user);
    sampleClass.setCreationTime(now);
    sampleClass.setLastModifier(user);
    sampleClass.setLastModified(now);
    long savedId = sut.create(sampleClass);

    clearSession();

    SampleClass saved = (SampleClass) currentSession().get(SampleClassImpl.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Alias";
    SampleClass sampleClass = (SampleClass) currentSession().get(SampleClassImpl.class, id);
    assertNotEquals(alias, sampleClass.getAlias());
    sampleClass.setAlias(alias);
    sut.update(sampleClass);

    clearSession();

    SampleClass saved = (SampleClass) currentSession().get(SampleClassImpl.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    SampleClass sampleClass = (SampleClass) currentSession().get(SampleClassImpl.class, 2L);
    assertEquals("Primary Tumor Tissue", sampleClass.getAlias());
    assertEquals(5L, sut.getUsage(sampleClass));
  }

}
