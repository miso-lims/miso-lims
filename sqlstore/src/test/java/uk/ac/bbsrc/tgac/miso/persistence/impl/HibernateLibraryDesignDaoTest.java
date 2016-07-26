package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;

public class HibernateLibraryDesignDaoTest extends AbstractDAOTest {
  
  @Autowired
  private SessionFactory sessionFactory;
  
  private HibernateLibraryDesignDao dao;
  
  @Before
  public void setup() {
    dao = new HibernateLibraryDesignDao();
    dao.setSessionFactory(sessionFactory);
  }
  
  @Test
  public void testGetLibraryDesigns() {
    List<LibraryDesign> list = dao.getLibraryDesigns();
    assertEquals(2, list.size());
  }
  
  @Test
  public void testGetLibraryDesignByClass() {
    SampleClass sc = new SampleClassImpl();
    sc.setId(1L);
    List<LibraryDesign> list = dao.getLibraryDesignByClass(sc);
    assertEquals(1L, list.size());
  }
  
  @Test
  public void testGetLibraryDesignByClassNull() {
    List<LibraryDesign> list = dao.getLibraryDesignByClass(null);
    assertNotNull(list);
    assertEquals(0, list.size());
  }
  
  @Test
  public void testGetLibraryDesign() {
    LibraryDesign ld = dao.getLibraryDesign(1L);
    assertNotNull(ld);
    assertEquals(Long.valueOf(1L), ld.getId());
    assertEquals("DESIGN1", ld.getName());
  }

}
