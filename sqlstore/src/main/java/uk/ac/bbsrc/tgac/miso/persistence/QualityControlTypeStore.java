package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QualityControlTypeStore extends SaveDao<QcType> {

  QcType getByNameAndTarget(String name, QcTarget target) throws IOException;

  long getUsage(QcType qcType) throws IOException;

  QcControl getControl(long id) throws IOException;

  long createControl(QcControl control) throws IOException;

  void deleteControl(QcControl control) throws IOException;

  long getControlUsage(QcControl control) throws IOException;

  long getKitUsage(QcType qcType, KitDescriptor kit) throws IOException;

}
