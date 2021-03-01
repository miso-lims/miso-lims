package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.math.BigDecimal;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateQcDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class HibernateLibraryQcDaoIT extends AbstractHibernateQcDaoTest<LibraryQC, HibernateLibraryQcDao, Library, LibraryQcControlRun> {

  public HibernateLibraryQcDaoIT() {
    super(LibraryQC.class, LibraryImpl.class, LibraryQcControlRun.class, QcTarget.Library, 14L, 10L, 10L, 5L, 16L, 1L);
  }

  @Override
  public HibernateLibraryQcDao constructTestSubject() {
    return new HibernateLibraryQcDao();
  }

  @Override
  protected LibraryQC makeQc(Library entity) {
    LibraryQC qc = new LibraryQC();
    qc.setLibrary(entity);
    return qc;
  }

  @Override
  protected QcControlRun makeControlRun(LibraryQC qc) {
    LibraryQcControlRun controlRun = new LibraryQcControlRun();
    controlRun.setQc(qc);
    return controlRun;
  }

  @Override
  protected BigDecimal getConcentration(Library entity) {
    return entity.getConcentration();
  }

  @Override
  protected void setConcentration(Library entity, BigDecimal concentration) {
    entity.setConcentration(concentration);
  }

}
