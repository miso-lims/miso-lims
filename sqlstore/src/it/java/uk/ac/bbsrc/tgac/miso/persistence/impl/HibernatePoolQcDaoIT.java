package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.math.BigDecimal;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateQcDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class HibernatePoolQcDaoIT extends AbstractHibernateQcDaoTest<PoolQC, HibernatePoolQCDao, Pool, PoolQcControlRun> {

  public HibernatePoolQcDaoIT() {
    super(PoolQC.class, PoolImpl.class, PoolQcControlRun.class, QcTarget.Pool, 15L, 4L, 4L, 6L, 4L, 4L);
  }

  @Override
  public HibernatePoolQCDao constructTestSubject() {
    return new HibernatePoolQCDao();
  }

  @Override
  protected PoolQC makeQc(Pool entity) {
    PoolQC qc = new PoolQC();
    qc.setPool(entity);
    return qc;
  }

  @Override
  protected QcControlRun makeControlRun(PoolQC qc) {
    PoolQcControlRun controlRun = new PoolQcControlRun();
    controlRun.setQc(qc);
    return controlRun;
  }

  @Override
  protected BigDecimal getConcentration(Pool entity) {
    return entity.getConcentration();
  }

  @Override
  protected void setConcentration(Pool entity, BigDecimal concentration) {
    entity.setConcentration(concentration);
  }
  
}
