package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PartitionQCTypeDto;

@Controller
@RequestMapping("/partitionqctype")
public class PartitionQcTypeController extends AbstractTypeDataController<PartitionQCType, PartitionQCTypeDto> {

  @Autowired
  private PartitionQcTypeService partitionQcTypeService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public PartitionQcTypeController() {
    super("Partition QC Types", "partitionqctype", "partitionqctype");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(partitionQcTypeService.list(), model);
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
  protected ProviderService<PartitionQCType> getService() {
    return partitionQcTypeService;
  }

  @Override
  protected PartitionQCTypeDto toDto(PartitionQCType object) {
    return Dtos.asDto(object);
  }

  @Override
  protected PartitionQCTypeDto makeDto() {
    return new PartitionQCTypeDto();
  }

}
