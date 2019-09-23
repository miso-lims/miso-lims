package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;

public abstract class AbstractTypeDataController<T extends Identifiable, R> {

  private final ObjectMapper mapper = new ObjectMapper();

  private final String pluralType;
  private final String listTarget;
  private final String hotTarget;

  public AbstractTypeDataController(String pluralType, String listTarget, String hotTarget) {
    this.pluralType = pluralType;
    this.listTarget = listTarget;
    this.hotTarget = hotTarget;
  }

  protected final ModelAndView bulkCreate(Integer quantity, ModelMap model) throws IOException {
    if (quantity == null || quantity <= 0) {
      throw new RestException("Must specify quantity to create", Status.BAD_REQUEST);
    }
    ObjectNode config = makeBaseConfig();
    config.put("pageMode", "create");
    addHotConfig(config, mapper);
    addHotAttributes("Create " + pluralType, config, true, model);

    model.put("input", mapper.writeValueAsString(Collections.nCopies(quantity, makeDto())));
    return new ModelAndView("/WEB-INF/pages/handsontables.jsp", model);
  }

  protected final ModelAndView bulkEdit(String idString, ModelMap model) throws IOException {
    ObjectNode config = makeBaseConfig();
    config.put("pageMode", "edit");
    addHotConfig(config, mapper);
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

    return new ModelAndView("/WEB-INF/pages/handsontables.jsp", model);
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
    model.put("targetType", "HotTarget." + hotTarget);
    model.put("create", create);
  }

  /**
   * Override to provide additional config fro Handsontable. Default implementation does nothing
   * 
   * @param config
   * @param mapper
   */
  protected void addHotConfig(ObjectNode config, ObjectMapper mapper) throws IOException {
    // Does nothing
  }

  protected final <U, V> void addConfigArray(ObjectNode config, ObjectMapper mapper, String key, Collection<U> items,
      Function<U, V> toDto) {
    ArrayNode array = config.putArray(key);
    for (U item : items) {
      V dto = toDto.apply(item);
      JsonNode itemNode = mapper.valueToTree(dto);
      array.add(itemNode);
    }
  }

  protected abstract AuthorizationManager getAuthorizationManager();

  protected abstract ProviderService<T> getService();

  protected abstract R toDto(T object);

  protected abstract R makeDto();

}
