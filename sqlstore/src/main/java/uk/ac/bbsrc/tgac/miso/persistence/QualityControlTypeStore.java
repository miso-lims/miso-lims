package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QualityControlTypeStore {

  public Collection<QcType> list() throws IOException;

  public QcType get(long id) throws IOException;

  public long create(QcType qcType) throws IOException;

  public void update(QcType qcType) throws IOException;

  public long getUsage(QcType qcType) throws IOException;

}
