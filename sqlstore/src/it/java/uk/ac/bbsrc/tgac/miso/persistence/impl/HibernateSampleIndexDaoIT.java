package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndex;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public class HibernateSampleIndexDaoIT extends AbstractHibernateSaveDaoTest<SampleIndex, HibernateSampleIndexDao> {

  public HibernateSampleIndexDaoIT() {
    super(SampleIndex.class, 4L, 7);
  }

  @Override
  public HibernateSampleIndexDao constructTestSubject() {
    HibernateSampleIndexDao sut = new HibernateSampleIndexDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public SampleIndex getCreateItem() {
    SampleIndex index = new SampleIndex();
    index.setName("Test Index");
    index.setPosition(1);
    SampleIndexFamily family = (SampleIndexFamily) currentSession().find(SampleIndexFamily.class, 1L);
    index.setFamily(family);
    return index;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<SampleIndex, String> getUpdateParams() {
    return new UpdateParameters<SampleIndex, String>(3L, SampleIndex::getName, SampleIndex::setName, "updated");
  }

  @Test
  public void testGetByName() throws Exception {
    testGetBy(HibernateSampleIndexDao::getByName, "Index 2-002", SampleIndex::getName);
  }

  @Test
  public void testGetByFamilyPositionAndName() throws Exception {
    long familyId = 2L;
    int position = 1;
    String name = "Index 2-002";
    SampleIndexFamily family = (SampleIndexFamily) currentSession().find(SampleIndexFamily.class, familyId);
    SampleIndex index = getTestSubject().getByFamilyPositionAndName(family, position, name);
    assertNotNull(index);
    assertEquals(name, index.getName());
    assertEquals(position, index.getPosition());
    assertEquals(familyId, index.getFamily().getId());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateSampleIndexDao::listByIdList, Lists.newArrayList(1L, 3L, 5L));
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateSampleIndexDao::getUsage, 3L, 1L);
  }

}
