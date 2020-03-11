package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QualityControlTypeStore {

  public Collection<QcType> list() throws IOException;

  public QcType get(long id) throws IOException;

  public List<QcType> listByNameAndTarget(String name, QcTarget target) throws IOException;

  public long create(QcType qcType) throws IOException;

  public void update(QcType qcType) throws IOException;

  public long getUsage(QcType qcType) throws IOException;

  public QcControl getControl(long id) throws IOException;

  public long createControl(QcControl control) throws IOException;

  public void deleteControl(QcControl control) throws IOException;

  public long getControlUsage(QcControl control) throws IOException;

  public long getKitUsage(QcType qcType, KitDescriptor kit) throws IOException;

}
