package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.persistence.SampleQcStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleQcDao extends HibernateQcStore<SampleQC> implements SampleQcStore {

  public HibernateSampleQcDao() {
    super(SampleImpl.class, SampleQC.class);
  }

  @Override
  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
      throws IOException {
    SampleImpl sample = (SampleImpl) currentSession().get(SampleImpl.class, id);
    sample.updateFromQc(correspondingField, value, units);
    currentSession().update(sample);
  }

  @Override
  public String getIdProperty() {
    return SampleImpl_.SAMPLE_ID;
  }

}
