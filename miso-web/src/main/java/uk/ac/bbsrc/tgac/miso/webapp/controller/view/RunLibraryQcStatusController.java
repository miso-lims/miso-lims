package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.RunLibraryQcStatusService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RunLibraryQcStatusDto;

@Controller
@RequestMapping("/runlibraryqcstatus")
public class RunLibraryQcStatusController extends AbstractTypeDataController<RunLibraryQcStatus, RunLibraryQcStatusDto> {

  @Autowired
  private RunLibraryQcStatusService runLibraryQcStatusService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public RunLibraryQcStatusController() {
    super("Run-Library QC Statuses", "runlibraryqcstatus", "runlibraryqcstatus");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(runLibraryQcStatusService.list(), model);
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model);
  }

  @PostMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    return bulkEdit(formData, model);
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<RunLibraryQcStatus> getService() {
    return runLibraryQcStatusService;
  }

  @Override
  protected RunLibraryQcStatusDto toDto(RunLibraryQcStatus object) {
    return Dtos.asDto(object);
  }

  @Override
  protected RunLibraryQcStatusDto makeDto() {
    return new RunLibraryQcStatusDto();
  }

}
