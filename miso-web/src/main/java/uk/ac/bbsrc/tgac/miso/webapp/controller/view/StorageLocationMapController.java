package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationMapService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StorageLocationMapDto;

@Controller
@RequestMapping("/locationmap")
public class StorageLocationMapController extends AbstractTypeDataController<StorageLocationMap, StorageLocationMapDto> {

  public StorageLocationMapController() {
    super("Location Maps", "locationmap", null);
  }

  @Autowired
  private StorageLocationMapService mapService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(mapService.list(), model);
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<StorageLocationMap> getService() {
    return mapService;
  }

  @Override
  protected StorageLocationMapDto toDto(StorageLocationMap object) {
    return Dtos.asDto(object);
  }

  @Override
  protected StorageLocationMapDto makeDto() {
    return new StorageLocationMapDto();
  }

}
