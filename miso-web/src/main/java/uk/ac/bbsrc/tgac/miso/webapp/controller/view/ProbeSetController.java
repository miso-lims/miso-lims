package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;
import uk.ac.bbsrc.tgac.miso.core.service.ProbeSetService;
import uk.ac.bbsrc.tgac.miso.dto.ProbeDto;
import uk.ac.bbsrc.tgac.miso.dto.ProbeSetDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/probeset")
public class ProbeSetController {

  @Autowired
  private ProbeSetService probeSetService;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    model.put("title", "Probe Sets");
    Stream<ProbeSetDto> dtos = probeSetService.list().stream().map(ProbeSetDto::from);
    return new ListItemsPage("probeset", mapper).list(model, dtos);
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New Probe Set");
    return setupForm(new ProbeSet(), PageMode.CREATE, model);
  }

  @GetMapping("/{id}")
  public ModelAndView edit(@PathVariable long id, ModelMap model) throws IOException {
    ProbeSet probeSet = retrieve(id);
    model.put("title", "Probe Set " + id);
    return setupForm(probeSet, PageMode.EDIT, model);
  }

  private ModelAndView setupForm(ProbeSet probeSet, PageMode pageMode, ModelMap model) throws IOException {
    model.put(PageMode.PROPERTY, pageMode.getLabel());
    model.put("probeSetDto", mapper.writeValueAsString(ProbeSetDto.from(probeSet)));
    return new ModelAndView("/WEB-INF/pages/editProbeSet.jsp", model);
  }

  private static final class BulkEditProbesBackend extends BulkTableBackend<ProbeDto> {

    private final ProbeSet probeSet;

    public BulkEditProbesBackend(ProbeSet probeSet, ObjectMapper mapper) {
      super("probe", ProbeDto.class, mapper);
      this.probeSet = probeSet;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("probeSetId", probeSet.getId());
    }

    public ModelAndView edit(Integer additionalProbes, ModelMap model) throws IOException {
      List<ProbeDto> dtos =
          probeSet.getProbes().stream().map(ProbeDto::from).collect(Collectors.toCollection(ArrayList::new));
      if (additionalProbes != null) {
        for (int i = 0; i < additionalProbes; i++) {
          dtos.add(new ProbeDto());
        }
      }
      return prepare(model, PageMode.EDIT, "Edit Probe Set Probes", dtos);
    }

  }

  @GetMapping("/{probeSetId}/probes")
  public ModelAndView getBulkEditProbesPage(@PathVariable long probeSetId,
      @RequestParam(required = false) Integer addProbes, ModelMap model) throws IOException {
    ProbeSet probeSet = retrieve(probeSetId);
    return new BulkEditProbesBackend(probeSet, mapper).edit(addProbes, model);
  }

  private ProbeSet retrieve(long id) throws IOException {
    ProbeSet probeSet = probeSetService.get(id);
    if (probeSet == null) {
      throw new NotFoundException("No probe set found for ID: " + id);
    }
    return probeSet;
  }

}
