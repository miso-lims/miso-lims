package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.WorkstationDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;

@Controller
@RequestMapping("/workstation")
public class WorkstationController extends AbstractTypeDataController<Workstation, WorkstationDto> {

  @Autowired
  private WorkstationService workstationService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private ObjectMapper mapper;

  public WorkstationController() {
    super("Workstations", "workstation", "workstation");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(workstationService.list(), model);
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model);
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    return bulkEdit(formData, model);
  }

  @GetMapping("/{workstationId}")
  public ModelAndView viewWorkstation(@PathVariable(value = "workstationId") Long workstationId, ModelMap model)
      throws IOException {
    Workstation workstation = workstationService.get(workstationId);
    if (workstation == null) {
      throw new NotFoundException("No workstation found for ID " + workstationId.toString());
    }
    model.put("title", "Workstation " + workstation.getId());
    return setupForm(workstation, model);
  }

  private ModelAndView setupForm(Workstation workstation, ModelMap model) throws IOException {
    WorkstationDto workstationDto = Dtos.asDto(workstation);
    model.put("workstation", workstation);
    model.put("workstationDto", mapper.writeValueAsString(workstationDto));

    return new ModelAndView("/WEB-INF/pages/editWorkstation.jsp", model);
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<Workstation> getService() {
    return workstationService;
  }

  @Override
  protected WorkstationDto toDto(Workstation object) {
    return Dtos.asDto(object);
  }

  @Override
  protected WorkstationDto makeDto() {
    return new WorkstationDto();
  }

}
