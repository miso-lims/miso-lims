package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;

public class HibernateSampleIndexFamilyDaoIT
    extends AbstractHibernateSaveDaoTest<SampleIndexFamily, HibernateSampleIndexFamilyDao> {

  public HibernateSampleIndexFamilyDaoIT() {
    super(SampleIndexFamily.class, 2L, 2);
  }

  @Override
  public HibernateSampleIndexFamilyDao constructTestSubject() {
    HibernateSampleIndexFamilyDao sut = new HibernateSampleIndexFamilyDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public SampleIndexFamily getCreateItem() {
    SampleIndexFamily family = new SampleIndexFamily();
    family.setName("New Family");
    return family;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<SampleIndexFamily, String> getUpdateParams() {
    return new UpdateParameters<SampleIndexFamily, String>(1L, SampleIndexFamily::getName, SampleIndexFamily::setName,
        "Changed");
  }

  @Test
  public void testGetByName() throws Exception {
    testGetBy(HibernateSampleIndexFamilyDao::getByName, "Fam One", SampleIndexFamily::getName);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateSampleIndexFamilyDao::getUsage, 1L, 1L);
  }

}
