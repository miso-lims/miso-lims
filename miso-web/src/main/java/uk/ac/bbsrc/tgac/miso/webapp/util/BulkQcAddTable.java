package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;

public class BulkQcAddTable extends BulkQcTable {

  private final int replicates;
  private final int controls;

  public BulkQcAddTable(QcTarget qcTarget, QualityControlService qcService, InstrumentService instrumentService, int replicates,
      int controls) {
    super(qcTarget, true, qcService, instrumentService, "Add");
    this.replicates = replicates;
    this.controls = controls;
  }

  @Override
  protected Stream<QcDto> load(long ownerId) throws IOException {
    QualityControlEntity entity = qcService.getEntity(qcTarget, ownerId);
    QcDto dto = new QcDto();
    dto.setEntityAlias(entity.getAlias());
    dto.setEntityId(entity.getId());
    return Stream.generate(() -> dto).limit(replicates);
  }

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    super.writeConfiguration(mapper, config);
    config.put("addControls", controls);
    config.put("pageMode", "create");
  }
}
