package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;

public abstract class BulkQcTable extends BulkTableBackend<QcDto> {
  private final boolean create;
  protected final QualityControlService qcService;
  private final InstrumentService instrumentService;
  protected final QcTarget qcTarget;
  private final String verb;

  public BulkQcTable(QcTarget qcTarget, boolean create, QualityControlService qcService,
      InstrumentService instrumentService, String verb, ObjectMapper mapper) {
    super("qc", QcDto.class, mapper);
    this.qcTarget = qcTarget;
    this.create = create;
    this.qcService = qcService;
    this.instrumentService = instrumentService;
    this.verb = verb;
  }

  public final ModelAndView display(String idString, ModelMap model) throws IOException {

    return prepare(model, create ? PageMode.CREATE : PageMode.EDIT, verb + " " + qcTarget + " QCs",
        LimsUtils.parseIds(idString).stream()
                .flatMap(WhineyFunction.rethrow(this::load))
                .sorted(Comparator.comparing(QcDto::getEntityAlias))
                .collect(Collectors.toList()));
  }

  protected abstract Stream<QcDto> load(long ownerId) throws IOException;

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    config.putPOJO("instruments", instrumentService.list().stream().map(Dtos::asDto).collect(Collectors.toList()));
    config.put("qcTarget", qcTarget.getLabel());
  }

}
