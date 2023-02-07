package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
@RequestMapping("/sop")
public class SopController extends AbstractTypeDataController<Sop, SopDto> {

  @Autowired
  private SopService sopService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public SopController() {
    super("SOPs", "sop", "sop");
  }

  @GetMapping("/list")
  public ModelAndView list(ModelMap model) throws IOException {
    model.put("title", "SOPs");
    TabbedListItemsPage listSops = new TabbedListItemsPage("sop", "category",
        Stream.of(SopCategory.values()), SopCategory::getLabel, SopCategory::name, mapper) {

      @Override
      protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
        config.put("isAdmin", authorizationManager.getCurrentUser().isAdmin());
      }

    };
    return listSops.list(model);
  }

  @PostMapping("/bulk/new")
  public ModelAndView create(@RequestParam Map<String, String> formData, ModelMap model) throws IOException {
    return bulkCreate(formData, model);
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
  protected ProviderService<Sop> getService() {
    return sopService;
  }

  @Override
  protected SopDto toDto(Sop object) {
    return Dtos.asDto(object);
  }

  @Override
  protected SopDto makeDto() {
    return new SopDto();
  }

}
