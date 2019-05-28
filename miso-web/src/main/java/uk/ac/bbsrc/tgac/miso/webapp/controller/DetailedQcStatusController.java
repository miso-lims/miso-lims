package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.DetailedQcStatusDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

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
    return listStatic(detailedQcStatusService.getAll(), model);
  }

  @GetMapping("/bulk/new")
  public ModelAndView create(@RequestParam("quantity") Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model);
  }

  @GetMapping("/bulk/edit")
  public ModelAndView edit(@RequestParam("ids") String idString, ModelMap model) throws IOException {
    return bulkEdit(idString, model);
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
