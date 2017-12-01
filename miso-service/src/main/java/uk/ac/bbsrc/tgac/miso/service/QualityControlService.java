package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QualityControlService {

  QC createQC(QC qc) throws IOException;

  QC get(QcTarget target, Long id) throws IOException;

  QualityControlEntity getEntity(QcTarget target, long ownerId) throws IOException;

  default QcType getQcType(QcTarget target, String name) throws IOException {
    return listQcTypes().stream().filter(qcType -> qcType.getQcTarget().equals(target) && qcType.getName().equals(name)).findFirst()
        .orElse(null);
  }

  Collection<? extends QC> listQCsFor(QcTarget target, long ownerId) throws IOException;

  Collection<QcType> listQcTypes() throws IOException;

  default Collection<QcType> listQcTypes(QcTarget target) throws IOException {
    return listQcTypes().stream().filter(qcType -> qcType.getQcTarget().equals(target)).collect(Collectors.toList());
  }

  QC updateQc(QC qc) throws IOException;

}
