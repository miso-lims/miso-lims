package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;

public interface QcTargetStore {

  public QC get(long id) throws IOException;

  public QualityControlEntity getEntity(long id) throws IOException;

  public Collection<? extends QC> listForEntity(long id) throws IOException;

  public List<? extends QC> listByIdList(List<Long> ids) throws IOException;

  public long save(QC qc) throws IOException;

  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units)
      throws IOException;

  public void deleteControlRun(QcControlRun controlRun) throws IOException;

  public long createControlRun(QcControlRun controlRun) throws IOException;

  public long updateControlRun(QcControlRun controlRun) throws IOException;

}
