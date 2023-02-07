package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkQcAddTable;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkQcEditTable;

@Controller
@RequestMapping("/qc")
public class QcController {

  @Autowired
  private QualityControlService qcService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private ObjectMapper mapper;

  @PostMapping("/bulk/addFrom/{qcTarget}")
  public ModelAndView addBulk(@PathVariable("qcTarget") String qcTargetLabel, @RequestParam Map<String, String> form, ModelMap model)
      throws IOException {
    QcTarget qcTarget = getQcTarget(qcTargetLabel);
    String entityIds = getStringInput("entityIds", form, true);
    int copies = getIntegerInput("copies", form, true);
    int controls = getIntegerInput("controls", form, true);
    return new BulkQcAddTable(qcTarget, qcService, instrumentService, copies, controls, mapper)
        .display(entityIds, model);
  }

  @PostMapping("/bulk/edit/{qcTarget}")
  public ModelAndView editBulk(@PathVariable("qcTarget") String qcTargetLabel, @RequestParam Map<String, String> form, ModelMap model)
      throws IOException {
    QcTarget qcTarget = getQcTarget(qcTargetLabel);
    String qcIds = getStringInput("ids", form, true);
    int addControls = getIntegerInput("addControls", form, true);
    return new BulkEditTableBackend<QC, QcDto>("qc", QcDto.class, "QCs", mapper) {

      @Override
      protected QcDto asDto(QC model) {
        return Dtos.asDto(model);
      }

      @Override
      protected Stream<QC> load(List<Long> modelIds) throws IOException {
        return modelIds.stream().map(WhineyFunction.rethrow(id -> qcService.get(qcTarget, id)));
      }

      @Override
      protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
        config.putPOJO("instruments", instrumentService.list().stream().map(Dtos::asDto).collect(Collectors.toList()));
        config.put("addControls", addControls);
        config.put("qcTarget", qcTarget.getLabel());
      }
    }.edit(qcIds, model);
  }

  @PostMapping("/bulk/editFrom/{qcTarget}")
  public ModelAndView editBulkFrom(@PathVariable("qcTarget") String qcTargetLabel, @RequestParam Map<String, String> form, ModelMap model)
      throws IOException {
    QcTarget qcTarget = getQcTarget(qcTargetLabel);
    String entityIds = getStringInput("entityIds", form, true);
    int addControls = getIntegerInput("addControls", form, true);
    return new BulkQcEditTable(qcTarget, qcService, instrumentService, addControls, mapper).display(entityIds, model);
  }

  private QcTarget getQcTarget(String label) {
    try {
      return QcTarget.valueOf(label);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new ClientErrorException("Invalid QC Target: " + label);
    }
  }

}
