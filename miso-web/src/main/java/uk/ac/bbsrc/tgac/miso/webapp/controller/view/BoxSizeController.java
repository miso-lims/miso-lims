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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxSizeService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.dto.BoxSizeDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/boxsize")
public class BoxSizeController extends AbstractTypeDataController<BoxSize, BoxSizeDto> {

  @Autowired
  private BoxSizeService boxSizeService;

  @Autowired
  private AuthorizationManager authorizationManager;

  public BoxSizeController() {
    super("Box Sizes", "boxsize", "boxsize");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    return listStatic(boxSizeService.list(), model);
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
  protected void addHotConfig(ObjectNode config, ObjectMapper mapper) throws IOException {
    ArrayNode boxTypes = config.putArray("boxTypes");
    for (BoxType boxType : BoxType.values()) {
      ObjectNode dto = boxTypes.addObject();
      dto.put("name", boxType.name());
      dto.put("label", boxType.getLabel());
    }
  }

  @Override
  protected AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  protected ProviderService<BoxSize> getService() {
    return boxSizeService;
  }

  @Override
  protected BoxSizeDto toDto(BoxSize object) {
    return Dtos.asDto(object);
  }

  @Override
  protected BoxSizeDto makeDto() {
    return new BoxSizeDto();
  }

}
