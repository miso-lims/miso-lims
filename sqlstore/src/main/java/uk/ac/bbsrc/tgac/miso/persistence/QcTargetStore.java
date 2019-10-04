package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;

public interface QcTargetStore {
  public QC get(long id) throws IOException;

  public QualityControlEntity getEntity(long id) throws IOException;

  public Collection<? extends QC> listForEntity(long id) throws IOException;

  public long save(QC qc) throws IOException;

  public void updateEntity(long id, QcCorrespondingField correspondingField, BigDecimal value, String units) throws IOException;
}