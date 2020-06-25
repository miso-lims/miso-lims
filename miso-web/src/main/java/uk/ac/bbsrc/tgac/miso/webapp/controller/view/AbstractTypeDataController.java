package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;

public abstract class AbstractTypeDataController<T extends Identifiable, R> {

  private static final String OLD_JSP = "/WEB-INF/pages/handsontables.jsp";
  private static final String NEW_JSP = "/WEB-INF/pages/bulkPage.jsp";

  private final ObjectMapper mapper = new ObjectMapper();

  private final String pluralType;
  private final String listTarget;
  private final String hotTarget;
  private final boolean newInterface;

  public AbstractTypeDataController(String pluralType, String listTarget, String hotTarget) {
    this(pluralType, listTarget, hotTarget, false);
  }

  public AbstractTypeDataController(String pluralType, String listTarget, String hotTarget, boolean newInterface) {
    this.pluralType = pluralType;
    this.listTarget = listTarget;
    this.hotTarget = hotTarget;
    this.newInterface = newInterface;
  }

  protected final ModelAndView bulkCreate(Integer quantity, ModelMap model) throws IOException {
    return bulkCreate(quantity, model, null);
  }

  protected final ModelAndView bulkCreate(Map<String, String> formData, ModelMap model) throws IOException {
    Integer quantity = getIntegerInput("quantity", formData, true);
    return bulkCreate(quantity, model, null);
  }

  protected final ModelAndView bulkCreate(Integer quantity, ModelMap model, BiConsumer<ObjectNode, ObjectMapper> configurer)
      throws IOException {
    if (quantity == null || quantity <= 0) {
      throw new RestException("Must specify quantity to create", Status.BAD_REQUEST);
    }
    ObjectNode config = makeBaseConfig();
    config.put("pageMode", "create");
    addHotConfig(config, mapper);
    if (configurer != null) {
      configurer.accept(config, mapper);
    }
    addHotAttributes("Create " + pluralType, config, true, model);

    model.put("input", mapper.writeValueAsString(Collections.nCopies(quantity, makeDto())));
    return new ModelAndView(newInterface ? NEW_JSP : OLD_JSP, model);
  }

  protected final ModelAndView bulkEdit(String idString, ModelMap model) throws IOException {
    return bulkEdit(idString, model, null);
  }

  protected final ModelAndView bulkEdit(Map<String, String> formData, ModelMap model) throws IOException {
    String idString = getStringInput("ids", formData, true);
    return bulkEdit(idString, model, null);
  }

  protected final ModelAndView bulkEdit(String idString, ModelMap model, BiConsumer<ObjectNode, ObjectMapper> configurer)
      throws IOException {
    ObjectNode config = makeBaseConfig();
    config.put("pageMode", "edit");
    addHotConfig(config, mapper);
    if (configurer != null) {
      configurer.accept(config, mapper);
    }
    addHotAttributes("Edit " + pluralType, config, false, model);

    List<Long> ids = LimsUtils.parseIds(idString);
    List<T> items = new ArrayList<>();
    for (Long id : ids) {
      T item = getService().get(id);
      if (item == null) {
        throw new ClientErrorException("No " + pluralType + " found with ID: " + id);
      }
      items.add(item);
    }
    model.put("input", mapper.writeValueAsString(items.stream().map(this::toDto).collect(Collectors.toList())));

    return new ModelAndView(newInterface ? NEW_JSP : OLD_JSP, model);
  }

  protected final ModelAndView listStatic(Collection<T> items, ModelMap model) throws IOException {
    ObjectNode config = makeBaseConfig();
    model.put("title", pluralType);
    model.put("data", mapper.writeValueAsString(items.stream().map(this::toDto).collect(Collectors.toList())));
    model.put("config", mapper.writeValueAsString(config));
    model.put("targetType", "ListTarget." + listTarget);
    return new ModelAndView("/WEB-INF/pages/listStatic.jsp", model);
  }

  private ObjectNode makeBaseConfig() throws IOException {
    ObjectNode config = mapper.createObjectNode();
    User user = getAuthorizationManager().getCurrentUser();
    config.put("isAdmin", user.isAdmin());
    return config;
  }

  private void addHotAttributes(String title, ObjectNode config, boolean create, ModelMap model) throws JsonProcessingException {
    model.put("title", title);
    model.put("config", mapper.writeValueAsString(config));
    if (newInterface) {
      model.put("target", hotTarget);
    } else {
      model.put("targetType", "HotTarget." + hotTarget);
      model.put("create", create);
    }
  }

  /**
   * Override to provide additional config for Handsontable. Default implementation does nothing
   * 
   * @param config
   * @param mapper
   */
  protected void addHotConfig(ObjectNode config, ObjectMapper mapper) throws IOException {
    // Does nothing
  }

  protected abstract AuthorizationManager getAuthorizationManager();

  protected abstract ProviderService<T> getService();

  protected abstract R toDto(T object);

  protected abstract R makeDto();

}
