package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;

import java.util.Arrays;

public class HibernateStudyTypeDaoIT extends AbstractHibernateSaveDaoTest<StudyType, HibernateStudyTypeDao> {

  public HibernateStudyTypeDaoIT() {
    super(StudyType.class, 4L, 4);
  }

  @Override
  public HibernateStudyTypeDao constructTestSubject() {
    HibernateStudyTypeDao sut = new HibernateStudyTypeDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public StudyType getCreateItem() {
    StudyType type = new StudyType();
    type.setName("I don't know");
    return type;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<StudyType, String> getUpdateParams() {
    return new UpdateParameters<>(3L, StudyType::getName, StudyType::setName, "Changed");
  }

  @Test
  public void testGetByName() throws Exception {
    String name = "Metagenomics";
    StudyType type = getTestSubject().getByName(name);
    assertNotNull(type);
    assertEquals(name, type.getName());
  }

  @Test
  public void testGetUsage() throws Exception {
    StudyType type1 = (StudyType) currentSession().get(StudyType.class, 1L);
    assertEquals(6, getTestSubject().getUsage(type1));
    StudyType type2 = (StudyType) currentSession().get(StudyType.class, 2L);
    assertEquals(0, getTestSubject().getUsage(type2));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateStudyTypeDao::listByIdList, Arrays.asList(2L, 3L));
  }

}
