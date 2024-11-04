package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest.PermittedSamples;

public class HibernateAssayTestDaoIT extends AbstractHibernateSaveDaoTest<AssayTest, HibernateAssayTestDao> {

  public HibernateAssayTestDaoIT() {
    super(AssayTest.class, 1L, 3);
  }

  @Override
  public HibernateAssayTestDao constructTestSubject() {
    HibernateAssayTestDao sut = new HibernateAssayTestDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public AssayTest getCreateItem() {
    AssayTest test = new AssayTest();
    test.setAlias("Test test");
    TissueType tissueType = (TissueType) currentSession().get(TissueTypeImpl.class, 1L);
    test.setTissueType(tissueType);
    SampleClass extractionClass = (SampleClass) currentSession().get(SampleClassImpl.class, 3L);
    test.setExtractionClass(extractionClass);
    LibraryDesignCode code = (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, 2L);
    test.setLibraryDesignCode(code);
    test.setLibraryQualificationMethod(AssayTest.LibraryQualificationMethod.LOW_DEPTH_SEQUENCING);
    test.setPermittedSamples(PermittedSamples.ALL);
    return test;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<AssayTest, String> getUpdateParams() {
    return new UpdateParameters<>(3L, AssayTest::getAlias, AssayTest::setAlias, "changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    testGetBy(HibernateAssayTestDao::getByAlias, "Normal WG", AssayTest::getAlias);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateAssayTestDao::getUsage, 2L, 2L);
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateAssayTestDao::listByIdList, Arrays.asList(1L, 2L));
  }
}
