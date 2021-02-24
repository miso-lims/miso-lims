package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSamplePurposeDaoIT extends AbstractHibernateSaveDaoTest<SamplePurpose, HibernateSamplePurposeDao> {

  public HibernateSamplePurposeDaoIT() {
    super(SamplePurposeImpl.class, 1L, 2);
  }

  @Override
  public HibernateSamplePurposeDao constructTestSubject() {
    HibernateSamplePurposeDao sut = new HibernateSamplePurposeDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public SamplePurpose getCreateItem() {
    SamplePurpose purpose = new SamplePurposeImpl();
    purpose.setAlias("New Purpose");
    User user = (User) currentSession().get(UserImpl.class, 1L);
    purpose.setChangeDetails(user);
    return purpose;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<SamplePurpose, String> getUpdateParams() {
    return new UpdateParameters<>(2L, SamplePurpose::getAlias, SamplePurpose::setAlias, "Changed");
  }

  @Test
  public void testGetUsage() throws Exception {
    SamplePurpose purpose1 = (SamplePurpose) currentSession().get(SamplePurposeImpl.class, 1L);
    assertEquals(1, getTestSubject().getUsage(purpose1));
    SamplePurpose purpose2 = (SamplePurpose) currentSession().get(SamplePurposeImpl.class, 2L);
    assertEquals(0, getTestSubject().getUsage(purpose2));
  }

}
