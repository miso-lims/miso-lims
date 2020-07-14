package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;

public class BulkQcEditTable extends BulkQcTable {

  private final int addControls;

  public BulkQcEditTable(QcTarget qcTarget, QualityControlService qcService, InstrumentService instrumentService, int addControls) {
    super(qcTarget, false, qcService, instrumentService, "Edit");
    this.addControls = addControls;
  }

  @Override
  protected Stream<QcDto> load(long ownerId) throws IOException {
    return qcService.listQCsFor(qcTarget, ownerId).stream().map(Dtos::asDto);
  }

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    super.writeConfiguration(mapper, config);
    config.put("addControls", addControls);
    config.put("pageMode", "edit");
  }

}
