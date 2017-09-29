package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateIndexDaoTest extends AbstractDAOTest {
  private HibernateIndexDao dao;
  @Autowired
  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    dao = new HibernateIndexDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGetIndexById() throws Exception {
    Index indexById = dao.getIndexById(8);
    assertEquals(8L, indexById.getId());
    assertEquals("ACTTGA", indexById.getSequence());
    assertEquals("Index 8", indexById.getName());
    assertEquals("TruSeq Single Index", indexById.getFamily().getName());

  }

  @Test
  public void testListIndicesByPlatform() throws Exception {
    Collection<Index> illumina = dao.list(0, 0, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertTrue(illumina.size() > 0);
  }

  @Test
  public void testListIndicesByStrategyName() throws Exception {
    IndexFamily list = dao.getIndexFamilyByName("Nextera Dual Index");
    assertTrue(20 == list.getIndices().size());
  }

  @Test
  public void testListAllIndices() throws Exception {
    List<Index> list = dao.list(0, 0, true, "id");
    assertEquals(80, list.size());
  }

  @Test
  public void testGetIndexFamilies() throws Exception {
    List<IndexFamily> list = dao.getIndexFamilies();
    assertEquals(12, list.size());
  }

  @Test
  public void testGetIndexFamiliesByPlatform() throws Exception {
    List<IndexFamily> list = dao.getIndexFamiliesByPlatform(PlatformType.ILLUMINA);
    assertEquals(11, list.size());
    int totalIlluminaIndices = 0;
    for (IndexFamily fam : list) {
      totalIlluminaIndices += fam.getIndices().size();
    }
    assertEquals(68, totalIlluminaIndices);
  }
}
