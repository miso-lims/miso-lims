package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcDto;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkQcAddTable;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkQcEditTable;

@Controller
@RequestMapping("/qc")
public class QcController {
  @Autowired
  private QualityControlService qcService;

  @RequestMapping(value = "/bulk/addFrom/{qcTarget}", method = RequestMethod.GET)
  public ModelAndView addBulk(@PathVariable("qcTarget") String qcTarget, @RequestParam("entityIds") String entityIds,
      @RequestParam("copies") int copies,
      ModelMap model) throws IOException {
    return new BulkQcAddTable(QcTarget.valueOf(qcTarget), qcService, copies).display(entityIds, model);
  }

  @RequestMapping(value = "/bulk/edit/{qcTarget}", method = RequestMethod.GET)
  public ModelAndView editBulk(@PathVariable("qcTarget") String qcTargetName, @RequestParam("ids") String qcIds, ModelMap model)
      throws IOException {
    QcTarget qcTarget = QcTarget.valueOf(qcTargetName);
    return new BulkEditTableBackend<QC, QcDto>("qc('" + qcTarget.name() + "')", QcDto.class, "QCs") {

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
        config.put("edit", true);
      }
    }.edit(qcIds, model);
  }

  @RequestMapping(value = "/bulk/editFrom/{qcTarget}", method = RequestMethod.GET)
  public ModelAndView editBulkFrom(@PathVariable("qcTarget") String qcTarget, @RequestParam("entityIds") String entityIds, ModelMap model)
      throws IOException {
    return new BulkQcEditTable(QcTarget.valueOf(qcTarget), qcService).display(entityIds, model);
  }

}
