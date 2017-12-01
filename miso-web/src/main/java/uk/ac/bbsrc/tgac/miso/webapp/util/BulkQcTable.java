package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;

public abstract class BulkQcTable extends BulkTableBackend<QcDto> {
  private final boolean create;
  protected final QualityControlService qcService;
  protected final QcTarget qcTarget;
  private final String verb;

  public BulkQcTable(QcTarget qcTarget, boolean create, QualityControlService qcService, String verb) {
    super("qc('" + qcTarget.name() + "')", QcDto.class);
    this.qcTarget = qcTarget;
    this.create = create;
    this.qcService = qcService;
    this.verb = verb;
  }

  public final ModelAndView display(String idString, ModelMap model) throws IOException {

    return prepare(model, create, verb + " " + qcTarget + " QCs",
        parseIds(idString).stream().flatMap(WhineyFunction.rethrow(this::load))
            .collect(Collectors.toList()));
  }

  protected abstract Stream<QcDto> load(long ownerId) throws IOException;

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
  }

}
