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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.DetailedQcStatusDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/detailedqcstatus")
public class DetailedQcStatusController extends AbstractTypeDataController<DetailedQcStatus, DetailedQcStatusDto> {

  @Autowired
  private DetailedQcStatusService detailedQcStatusService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public DetailedQcStatusController() {
    super("Detailed QC Statuses", "detailedqcstatus", "detailedqcstatus");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(detailedQcStatusService.list(), model);
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
  protected ProviderService<DetailedQcStatus> getService() {
    return detailedQcStatusService;
  }

  @Override
  protected DetailedQcStatusDto toDto(DetailedQcStatus object) {
    return Dtos.asDto(object);
  }

  @Override
  protected DetailedQcStatusDto makeDto() {
    return new DetailedQcStatusDto();
  }

}
