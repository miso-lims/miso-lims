package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;

public class HibernateAssayDaoIT extends AbstractHibernateSaveDaoTest<Assay, HibernateAssayDao> {

  public HibernateAssayDaoIT() {
    super(Assay.class, 1L, 2);
  }

  @Override
  public HibernateAssayDao constructTestSubject() {
    HibernateAssayDao sut = new HibernateAssayDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public Assay getCreateItem() {
    Assay assay = new Assay();
    assay.setAlias("New Assay");
    assay.setVersion("1.0");
    return assay;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Assay, String> getUpdateParams() {
    return new UpdateParameters<>(1L, Assay::getAlias, Assay::setAlias, "Changed");
  }

  @Test
  public void testGetByAliasAndVersion() throws Exception {
    String alias = "Full Depth WGTS";
    String version = "1.0";
    Assay assay = getTestSubject().getByAliasAndVersion(alias, version);
    assertNotNull(assay);
    assertEquals(alias, assay.getAlias());
    assertEquals(version, assay.getVersion());
  }

  @Test
  public void testGetUsage() throws Exception {
    // TODO
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateAssayDao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testDeleteAssayMetric() throws Exception {
    // TODO
  }

}
