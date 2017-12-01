package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;

public interface QcTargetStore {
  public QC get(long id) throws IOException;

  public QualityControlEntity getEntity(long id) throws IOException;

  public Collection<? extends QC> listForEntity(long id) throws IOException;

  public long save(QC qc) throws IOException;
}