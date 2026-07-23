package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;

public class HibernateSampleValidRelationshipDaoIT extends AbstractDAOTest {

  private HibernateSampleValidRelationshipDao sut;

  @BeforeEach
  public void setup() {
    sut = new HibernateSampleValidRelationshipDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testList() throws Exception {
    List<SampleValidRelationship> relationships = sut.list();
    assertNotNull(relationships);
    assertEquals(6, relationships.size());
  }

  @Test
  public void testGet() throws Exception {
    long id = 2L;
    SampleValidRelationship relationship = sut.get(id);
    assertNotNull(relationship);
    assertEquals(id, relationship.getId());
  }

  @Test
  public void testGetByClasses() throws Exception {
    SampleClass parent = (SampleClass) currentSession().find(SampleClassImpl.class, 1L);
    SampleClass child = (SampleClass) currentSession().find(SampleClassImpl.class, 2L);
    SampleValidRelationship relationship = sut.getByClasses(parent, child);
    assertNotNull(relationship);
    assertEquals(parent.getId(), relationship.getParent().getId());
    assertEquals(child.getId(), relationship.getChild().getId());
  }

  @Test
  public void testDelete() throws Exception {
    SampleValidRelationship before =
        (SampleValidRelationship) currentSession().find(SampleValidRelationshipImpl.class, 4L);
    assertNotNull(before);
    sut.delete(before);

    clearSession();

    SampleValidRelationship after =
        (SampleValidRelationship) currentSession().find(SampleValidRelationshipImpl.class, 4L);
    assertNull(after);
  }

}
