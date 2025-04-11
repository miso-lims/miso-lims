package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.junit.Test;

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
    return new HibernateSampleIndexDao();
  }

  @Override
  public SampleIndex getCreateItem() {
    SampleIndex index = new SampleIndex();
    index.setName("Test Index");
    SampleIndexFamily family = (SampleIndexFamily) currentSession().get(SampleIndexFamily.class, 1L);
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
  public void testGetByFamilyAndName() throws Exception {
    long familyId = 2L;
    String name = "Index 2-002";
    SampleIndexFamily family = (SampleIndexFamily) currentSession().get(SampleIndexFamily.class, familyId);
    SampleIndex index = getTestSubject().getByFamilyAndName(family, name);
    assertNotNull(index);
    assertEquals(name, index.getName());
    assertEquals(familyId, index.getFamily().getId());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateSampleIndexDao::listByIdList, Lists.newArrayList(1L, 3L, 5L));
  }

}
