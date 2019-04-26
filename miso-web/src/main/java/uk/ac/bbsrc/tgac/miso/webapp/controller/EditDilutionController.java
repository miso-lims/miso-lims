package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.RunService;

@Controller
@RequestMapping("/dilution")
public class EditDilutionController {

  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;

  @GetMapping("/{dilutionId}")
  public ModelAndView editDilution(ModelMap model, @PathVariable long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionService.get(dilutionId);
    if (dilution == null) {
      throw new NotFoundException("Dilution not found");
    }

    ObjectMapper mapper = new ObjectMapper();
    model.put("dilution", dilution);
    model.put("dilutionDto", mapper.writeValueAsString(Dtos.asDto(dilution, false, false)));
    List<Pool> pools = poolService.listByDilutionId(dilutionId);
    model.put("dilutionPools",
        pools.stream().map(p -> Dtos.asDto(p, false, false)).collect(Collectors.toList()));
    model.put("dilutionRuns", pools.stream().flatMap(WhineyFunction.flatRethrow(p -> runService.listByPoolId(p.getId()))).map(Dtos::asDto)
        .collect(Collectors.toList()));

    return new ModelAndView("/WEB-INF/pages/editDilution.jsp", model);
  }

}
