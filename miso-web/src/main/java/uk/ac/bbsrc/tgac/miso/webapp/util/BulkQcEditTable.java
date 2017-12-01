package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;

public class BulkQcEditTable extends BulkQcTable {

  public BulkQcEditTable(QcTarget qcTarget, QualityControlService qcService) {
    super(qcTarget, false, qcService, "Edit");
  }

  @Override
  protected Stream<QcDto> load(long ownerId) throws IOException {
    return qcService.listQCsFor(qcTarget, ownerId).stream().map(Dtos::asDto);
  }
}
