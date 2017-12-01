package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;

public class HibernateLibraryDesignDaoTest extends AbstractDAOTest {
  
  @Autowired
  private SessionFactory sessionFactory;
  
  @Mock
  private LibraryStore libraryStore;

  @InjectMocks
  private HibernateLibraryDesignDao dao;
  
  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);

    LibrarySelectionType selType = new LibrarySelectionType();
    selType.setId(1L);
    Mockito.when(libraryStore.getLibrarySelectionTypeById(Mockito.anyLong())).thenReturn(selType);
    LibraryStrategyType stratType = new LibraryStrategyType();
    stratType.setId(1L);
    Mockito.when(libraryStore.getLibraryStrategyTypeById(Mockito.anyLong())).thenReturn(stratType);
  }
  
  @Test
  public void testGetLibraryDesigns() throws IOException {
    List<LibraryDesign> list = dao.getLibraryDesigns();
    assertEquals(2, list.size());
  }
  
  @Test
  public void testGetLibraryDesignByClass() throws IOException {
    SampleClass sc = new SampleClassImpl();
    sc.setId(1L);
    List<LibraryDesign> list = dao.getLibraryDesignByClass(sc);
    assertEquals(1L, list.size());
  }
  
  @Test
  public void testGetLibraryDesignByClassNull() throws IOException {
    List<LibraryDesign> list = dao.getLibraryDesignByClass(null);
    assertNotNull(list);
    assertEquals(0, list.size());
  }
  
  @Test
  public void testGetLibraryDesign() throws IOException {
    LibraryDesign ld = dao.getLibraryDesign(1L);
    assertNotNull(ld);
    assertEquals(Long.valueOf(1L), ld.getId());
    assertEquals("DESIGN1", ld.getName());
  }

}
