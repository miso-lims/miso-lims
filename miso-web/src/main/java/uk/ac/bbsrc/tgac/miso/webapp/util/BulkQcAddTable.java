package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;

public class BulkQcAddTable extends BulkQcTable {

  private final int replicates;

  public BulkQcAddTable(QcTarget qcTarget, QualityControlService qcService, int replicates) {
    super(qcTarget, true, qcService, "Add");
    this.replicates = replicates;
  }

  @Override
  protected Stream<QcDto> load(long ownerId) throws IOException {
    QualityControlEntity entity = qcService.getEntity(qcTarget, ownerId);
    QcDto dto = new QcDto();
    dto.setEntityAlias(entity.getAlias());
    dto.setEntityId(entity.getId());
    return Stream.generate(() -> dto).limit(replicates);
  }
}
