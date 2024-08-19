package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;

public class HibernateAssayDaoIT extends AbstractHibernateSaveDaoTest<Assay, HibernateAssayDao> {

  public HibernateAssayDaoIT() {
    super(Assay.class, 1L, 3);
  }

  @Override
  public HibernateAssayDao constructTestSubject() {
    HibernateAssayDao sut = new HibernateAssayDao();
    sut.setEntityManager(getEntityManager());
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
    Assay assay = (Assay) currentSession().get(Assay.class, 1L);
    assertEquals(1L, getTestSubject().getUsage(assay));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateAssayDao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testDeleteAssayMetric() throws Exception {
    final long assayId = 2L;
    final long metricId = 2L;

    Assay before = (Assay) currentSession().get(Assay.class, assayId);
    assertEquals(2, before.getAssayMetrics().size());
    AssayMetric beforeMetric =
        before.getAssayMetrics().stream().filter(x -> x.getMetric().getId() == metricId).findAny().orElse(null);
    before.getAssayMetrics().remove(beforeMetric);
    assertNotNull(beforeMetric);
    getTestSubject().deleteAssayMetric(beforeMetric);

    clearSession();

    Assay after = (Assay) currentSession().get(Assay.class, assayId);
    assertEquals(1, after.getAssayMetrics().size());
    assertTrue(after.getAssayMetrics().stream().noneMatch(x -> x.getMetric().getId() == metricId));
  }

}
