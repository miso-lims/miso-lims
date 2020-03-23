package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentDto;

@Controller
@RequestMapping("/arrayrun")
public class EditArrayRunController {

  private static final String JSP = "/WEB-INF/pages/editArrayRun.jsp";

  private static final String MODEL_ATTR_TITLE = "title";
  private static final String MODEL_ATTR_PAGEMODE = "pageMode";
  private static final String MODEL_ATTR_SCANNERS = "arrayScanners";
  private static final String MODEL_ATTR_JSON = "arrayRunJson";
  private static final String MODEL_ATTR_RUN = "arrayRun";

  @Autowired
  private ArrayRunService arrayRunService;

  @Autowired
  private InstrumentService instrumentService;

  @ModelAttribute("healthTypes")
  public Collection<String> populateHealthTypes() {
    return HealthType.getKeys();
  }

  @RequestMapping("/new")
  public ModelAndView newArrayRun(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "New Array Run");
    model.addAttribute(MODEL_ATTR_PAGEMODE, "create");

    ObjectMapper mapper = new ObjectMapper();
    model.addAttribute(MODEL_ATTR_SCANNERS, mapper.writeValueAsString(getArrayScannerDtos()));

    return new ModelAndView(JSP, model);
  }

  @RequestMapping("/{arrayRunId}")
  public ModelAndView setupForm(@PathVariable(name = "arrayRunId", required = true) long arrayRunId, ModelMap model) throws IOException {
    ArrayRun run = arrayRunService.get(arrayRunId);
    if (run == null) {
      throw new NotFoundException("Array Run not found");
    }
    model.addAttribute(MODEL_ATTR_TITLE, "Array Run " + arrayRunId);
    model.addAttribute(MODEL_ATTR_PAGEMODE, "edit");

    ObjectMapper mapper = new ObjectMapper();
    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(Dtos.asDto(run)));
    model.addAttribute(MODEL_ATTR_RUN, run);
    return new ModelAndView(JSP, model);
  }

  private List<InstrumentDto> getArrayScannerDtos() throws IOException {
    return instrumentService.list().stream()
        .filter(inst -> inst.getInstrumentModel().getInstrumentType() == InstrumentType.ARRAY_SCANNER)
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

}
