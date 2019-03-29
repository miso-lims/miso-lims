package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;

@Controller
@RequestMapping("/arrayrun")
public class EditArrayRunController {

  private static final String JSP = "/WEB-INF/pages/editArrayRun.jsp";

  private static final String MODEL_ATTR_PAGEMODE = "pageMode";
  private static final String MODEL_ATTR_SCANNERS = "arrayScanners";
  private static final String MODEL_ATTR_JSON = "arrayRunJson";

  @Autowired
  private ArrayRunService arrayRunService;

  @Autowired
  private InstrumentService instrumentService;

  @ModelAttribute("maxLengths")
  public Map<String, Integer> getColumnSizes() throws IOException {
    return arrayRunService.getColumnSizes();
  }

  @ModelAttribute("healthTypes")
  public Collection<String> populateHealthTypes() {
    return HealthType.getKeys();
  }

  @RequestMapping("/new")
  public ModelAndView newArrayRun(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_PAGEMODE, "create");
    model.addAttribute(MODEL_ATTR_SCANNERS, getArrayScanners());
    return new ModelAndView(JSP, model);
  }

  @RequestMapping("/{arrayRunId}")
  public ModelAndView setupForm(@PathVariable(name = "arrayRunId", required = true) long arrayRunId, ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_PAGEMODE, "edit");
    ArrayRun run = arrayRunService.get(arrayRunId);
    if (run == null) {
      throw new NotFoundException("Array Run not found");
    }
    ObjectMapper mapper = new ObjectMapper();
    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(Dtos.asDto(run)));
    return new ModelAndView(JSP, model);
  }

  private List<Instrument> getArrayScanners() throws IOException {
    return instrumentService.list().stream()
        .filter(inst -> inst.getInstrumentModel().getInstrumentType() == InstrumentType.ARRAY_SCANNER)
        .collect(Collectors.toList());
  }

}
