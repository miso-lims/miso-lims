package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.persistence.PoolQcStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolQCDao extends HibernateQcStore<PoolQC> implements PoolQcStore {

  public HibernatePoolQCDao() {
    super(PoolImpl.class, PoolQC.class);
  }

  @Override
  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
      throws IOException {
    Pool pool = (Pool) currentSession().get(PoolImpl.class, id);
    correspondingField.updateField(pool, value, units);
    currentSession().merge(pool);
  }

  @Override
  public String getIdProperty() {
    return PoolImpl_.POOL_ID;
  }

}
