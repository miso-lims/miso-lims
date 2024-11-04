package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;

public class HibernateSopDaoIT extends AbstractHibernateSaveDaoTest<Sop, HibernateSopDao> {

  public HibernateSopDaoIT() {
    super(Sop.class, 1L, 5);
  }

  @Override
  public HibernateSopDao constructTestSubject() {
    HibernateSopDao sut = new HibernateSopDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Sop getCreateItem() {
    Sop sop = new Sop();
    sop.setAlias("Test SOP");
    sop.setVersion("1.0");
    sop.setCategory(SopCategory.SAMPLE);
    sop.setUrl("http://sops.test.com/test_sop");
    sop.setArchived(false);
    return sop;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Sop, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Sop::getAlias, Sop::setAlias, "Updated Alias");
  }

  @Test
  public void testGetByAliasAndVersion() throws Exception {
    SopCategory category = SopCategory.SAMPLE;
    String alias = "Sample SOP 1";
    String version = "1.0";
    Sop sop = getTestSubject().get(category, alias, version);
    assertNotNull(sop);
    assertEquals(category, sop.getCategory());
    assertEquals(alias, sop.getAlias());
    assertEquals(version, sop.getVersion());
  }

  @Test
  public void testListByCategory() throws Exception {
    List<Sop> sops = getTestSubject().listByCategory(SopCategory.LIBRARY);
    assertNotNull(sops);
    assertEquals(2, sops.size());
    for (Sop sop : sops) {
      assertEquals(SopCategory.LIBRARY, sop.getCategory());
    }
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateSopDao::listByIdList, Lists.newArrayList(3L, 4L));
  }

  @Test
  public void testGetUsageBySamples() throws Exception {
    testGetUsage(HibernateSopDao::getUsageBySamples, 1L, 1L);
  }

  @Test
  public void testGetUsageByLibraries() throws Exception {
    testGetUsage(HibernateSopDao::getUsageByLibraries, 3L, 1L);
  }

  @Test
  public void testGetUsageByRuns() throws Exception {
    testGetUsage(HibernateSopDao::getUsageByRuns, 5L, 1L);
  }

}
