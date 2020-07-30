package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface QualityControlService {

  public QC create(QC qc) throws IOException;

  public QC update(QC qc) throws IOException;

  public QC get(QcTarget target, Long id) throws IOException;

  public QualityControlEntity getEntity(QcTarget target, long ownerId) throws IOException;

  public default QcType getQcType(QcTarget target, String name) throws IOException {
    return listQcTypes().stream().filter(qcType -> qcType.getQcTarget().equals(target) && qcType.getName().equals(name)).findFirst()
        .orElse(null);
  }

  public Collection<? extends QC> listQCsFor(QcTarget target, long ownerId) throws IOException;

  public Collection<QcType> listQcTypes() throws IOException;

  public default Collection<QcType> listQcTypes(QcTarget target) throws IOException {
    return listQcTypes().stream().filter(qcType -> qcType.getQcTarget().equals(target)).collect(Collectors.toList());
  }

  public List<? extends QC> listByIdList(QcTarget qcTarget, List<Long> ids) throws IOException;

  public BulkQcSaveOperation startBulkCreate(List<QC> items) throws IOException;

  public BulkQcSaveOperation startBulkUpdate(List<QC> items) throws IOException;

}
